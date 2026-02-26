package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @DisplayName("수동 출결을 EXCUSED로 등록하면 공결 횟수가 증가한다")
    @Test
    void createManualExcusedIncrementsExcuseCount() {
        Session session = sessionService.create(new SessionDtos.CreateServiceCommand(
                Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1))
        )).session();
        CohortMember cohortMember = createCohortMember();

        Attendance attendance = attendanceService.createManual(
                new AttendanceDtos.CreateManualCommand(session, cohortMember, AttendanceStatus.EXCUSED)
        ).attendance();

        CohortMember found = cohortMemberRepository.findById(cohortMember.getId()).orElseThrow();
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.EXCUSED);
        assertThat(found.getExcuseCount()).isEqualTo(1);
    }

    @DisplayName("이미 출결이 존재하면 중복 체크를 거부한다")
    @Test
    void validateNotExistsThrowsWhenAlreadyExists() {
        Session session = sessionService.create(new SessionDtos.CreateServiceCommand(
                Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1))
        )).session();
        CohortMember cohortMember = createCohortMember();
        attendanceService.createManual(new AttendanceDtos.CreateManualCommand(session, cohortMember, AttendanceStatus.PRESENT));

        assertThatThrownBy(() -> attendanceService.validateNotExists(new AttendanceDtos.ValidateNotExistsCommand(session.getId(), cohortMember.getId())))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
    }

    private CohortMember createCohortMember() {
        String loginId = "attendance-" + System.nanoTime();
        Member member = memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name")).member();
        Cohort cohort = cohortService.findCurrentCohort().cohort();
        Part part = partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(cohort.getId()))
                .findFirst()
                .orElseThrow();
        return memberService.saveCohortMember(new MemberDtos.SaveCohortMemberCommand(
                CohortMember.create(member.getId(), cohort.getId(), part.getId(), null, 100000)
        )).cohortMember();
    }
}
