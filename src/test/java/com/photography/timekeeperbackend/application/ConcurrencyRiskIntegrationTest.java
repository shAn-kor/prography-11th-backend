package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.PartType;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.attendance.AttendanceRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.deposit.DepositHistoryRepository;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import com.photography.timekeeperbackend.domain.repository.qrcode.QRCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ConcurrencyRiskIntegrationTest {

    @Autowired
    private AttendanceFacade attendanceFacade;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private DepositHistoryRepository depositHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @DisplayName("출석 중복은 유니크 제약 기반으로 1건만 생성되고 나머지는 도메인 충돌로 처리된다")
    @Test
    void attendance_duplicate_request_is_mapped_to_business_conflict() throws Exception {
        Session session = createInProgressSession(LocalDateTime.now().plusHours(1));
        CohortMember cohortMember = createCohortMember(100000);
        QRCode qrCode = qrCodeService.create(new QRCodeDtos.CreateCommand(session)).qrCode();
        Member member = memberService.findMemberByCohortMemberId(
                new MemberDtos.FindMemberByCohortMemberIdCommand(cohortMember.getId())
        ).member();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        runConcurrently(2, () -> {
            try {
                attendanceFacade.checkAttendance(new AttendanceDtos.CheckCommand(qrCode.getHashValue(), member.getId()));
                successCount.incrementAndGet();
            } catch (RuntimeException ex) {
                if (ex instanceof BusinessException businessException
                        && businessException.getErrorCode() == ErrorCode.ATTENDANCE_ALREADY_CHECKED) {
                    conflictCount.incrementAndGet();
                    return;
                }
                unexpected.add(ex);
            }
        });

        assertThat(unexpected).isEmpty();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(1);
        assertThat(attendanceRepository.findBySessionIdAndCohortMemberId(session.getId(), cohortMember.getId())).isPresent();
    }

    @DisplayName("보증금은 낙관적 락 충돌이 발생하면 일부 요청이 실패하고 잔액은 일관성을 유지한다")
    @Test
    void deposit_concurrent_update_uses_optimistic_lock() throws Exception {
        CohortMember saved = createCohortMember(100000);
        CountDownLatch bothLoaded = new CountDownLatch(2);
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();

        runConcurrently(2, () -> {
            try {
                template.executeWithoutResult(status -> {
                    CohortMember loaded = cohortMemberRepository.findById(saved.getId()).orElseThrow();
                    bothLoaded.countDown();
                    await(bothLoaded);
                    loaded.deductDeposit(10000);
                });
                successCount.incrementAndGet();
            } catch (RuntimeException ex) {
                if (isCausedBy(ex, ObjectOptimisticLockingFailureException.class)) {
                    conflictCount.incrementAndGet();
                    return;
                }
                throw ex;
            }
        });

        int finalDeposit = cohortMemberRepository.findById(saved.getId()).orElseThrow().getDeposit();
        assertThat(finalDeposit).isEqualTo(90000);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(1);
    }

    @DisplayName("QR 활성 1개 보장은 세션 비관적 락으로 동시 발급 충돌을 방지한다")
    @Test
    void qrcode_concurrent_create_allows_only_one_active_qr() throws Exception {
        Session session = createInProgressSession(LocalDateTime.now().plusHours(2));
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        runConcurrently(2, () -> {
            try {
                qrCodeService.create(new QRCodeDtos.CreateCommand(session));
                successCount.incrementAndGet();
            } catch (RuntimeException ex) {
                if (ex instanceof BusinessException businessException
                        && businessException.getErrorCode() == ErrorCode.QR_ALREADY_ACTIVE) {
                    conflictCount.incrementAndGet();
                    return;
                }
                unexpected.add(ex);
            }
        });

        assertThat(unexpected).isEmpty();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(1);
        assertThat(qrCodeRepository.findActiveBySessionId(session.getId(), LocalDateTime.now())).isPresent();
    }

    @DisplayName("Facade 유스케이스를 단일 트랜잭션으로 묶으면 후속 실패 시 선행 변경이 롤백된다")
    @Test
    void facade_orchestration_rolls_back_when_penalty_deduct_fails() {
        Session session = createInProgressSession(LocalDateTime.now().minusHours(5));
        CohortMember lowDepositMember = createCohortMember(0);
        QRCode qrCode = qrCodeService.create(new QRCodeDtos.CreateCommand(session)).qrCode();
        Member member = memberService.findMemberByCohortMemberId(
                new MemberDtos.FindMemberByCohortMemberIdCommand(lowDepositMember.getId())
        ).member();

        assertThatThrownBy(() -> attendanceFacade.checkAttendance(
                new AttendanceDtos.CheckCommand(qrCode.getHashValue(), member.getId())
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("보증금 잔액이 부족");

        assertThat(attendanceRepository.findBySessionIdAndCohortMemberId(session.getId(), lowDepositMember.getId())).isEmpty();
        assertThat(depositHistoryRepository.findAllByCohortMemberIdOrderByCreatedAtDesc(lowDepositMember.getId())).isEmpty();
    }

    @DisplayName("회원 중복 가입은 유니크 제약 충돌을 도메인 충돌로 변환한다")
    @Test
    void member_duplicate_signup_is_mapped_to_business_conflict() throws Exception {
        String loginId = "race-login-" + System.nanoTime();
        String rawPassword = "pw1234";
        String name = "race-member";
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger businessConflictCount = new AtomicInteger();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        runConcurrently(2, () -> {
            try {
                memberService.createMember(new MemberDtos.CreateCommand(loginId, rawPassword, name));
                successCount.incrementAndGet();
            } catch (RuntimeException ex) {
                if (ex instanceof BusinessException businessException
                        && businessException.getErrorCode() == ErrorCode.LOGIN_ID_DUPLICATED) {
                    businessConflictCount.incrementAndGet();
                    return;
                }
                unexpected.add(ex);
            }
        });

        assertThat(unexpected).isEmpty();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(businessConflictCount.get()).isEqualTo(1);
        assertThat(memberRepository.findByLoginId(loginId)).isPresent();
    }

    private Session createInProgressSession(LocalDateTime sessionDate) {
        Cohort cohort = cohortService.findCurrentCohort().cohort();
        Session session = Session.create(
                cohort,
                "concurrency-session-" + System.nanoTime(),
                "desc",
                SessionType.STUDY,
                sessionDate
        );
        Session saved = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        return sessionService.updateStatus(new SessionDtos.UpdateStatusCommand(saved.getId(), SessionStatus.IN_PROGRESS)).session();
    }

    private CohortMember createCohortMember(int deposit) {
        String loginId = "concurrency-member-" + System.nanoTime();
        Member member = memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name")).member();
        Part part = currentCohortPart();
        return memberService.saveCohortMember(new MemberDtos.SaveCohortMemberCommand(
                CohortMember.create(member.getId(), part.getCohort().getId(), part.getId(), null, deposit)
        )).cohortMember();
    }

    private Part currentCohortPart() {
        Long currentCohortId = cohortService.findCurrentCohort().cohort().getId();
        return partRepository.findAll().stream()
                .filter(part -> part.getCohort().getId().equals(currentCohortId))
                .filter(part -> part.getType() == PartType.SERVER)
                .findFirst()
                .orElseThrow();
    }

    private void runConcurrently(int workers, Runnable task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(workers);
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(workers);
        List<Throwable> failures = new CopyOnWriteArrayList<>();
        try {
            for (int i = 0; i < workers; i++) {
                executor.submit(() -> {
                    ready.countDown();
                    await(start);
                    try {
                        task.run();
                    } catch (Throwable throwable) {
                        failures.add(throwable);
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
            assertThat(failures).isEmpty();
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("latch timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private boolean isCausedBy(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
