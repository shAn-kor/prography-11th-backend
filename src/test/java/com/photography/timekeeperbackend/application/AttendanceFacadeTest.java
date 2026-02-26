package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AttendanceFacadeTest {

    @Autowired
    private AttendanceFacade attendanceFacade;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private PartRepository partRepository;

    @DisplayName("출석 체크 퍼사드는 QR 검증부터 출결 생성까지 수행한다")
    @Test
    void checkAttendanceCreatesAttendance() {
        Cohort cohort = cohortService.findCurrentCohort().cohort();
        Session session = Session.create(cohort, "title", "desc", SessionType.STUDY, LocalDateTime.now().plusMinutes(5));
        Session savedSession = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        sessionService.updateStatus(new SessionDtos.UpdateStatusCommand(savedSession.getId(), SessionStatus.IN_PROGRESS));
        QRCode qrCode = qrCodeService.create(new QRCodeDtos.CreateCommand(savedSession)).qrCode();

        String loginId = "facade-attendance-" + System.nanoTime();
        Member member = memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name")).member();
        Part part = partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(cohort.getId()))
                .findFirst()
                .orElseThrow();
        CohortMember cohortMember = memberService.saveCohortMember(new MemberDtos.SaveCohortMemberCommand(
                CohortMember.create(member.getId(), cohort.getId(), part.getId(), null, 100000)
        )).cohortMember();

        Attendance attendance = attendanceFacade.checkAttendance(new AttendanceDtos.CheckCommand(qrCode.getHashValue(), member.getId())).attendance();

        assertThat(attendance.getSessionId()).isEqualTo(savedSession.getId());
        assertThat(attendance.getCohortMemberId()).isEqualTo(cohortMember.getId());
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    }
}
