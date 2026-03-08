package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import com.photography.timekeeperbackend.application.dto.DepositDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class AttendanceFacade {

    private final AttendanceService attendanceService;
    private final QRCodeService qrCodeService;
    private final SessionService sessionService;
    private final MemberService memberService;
    private final DepositService depositService;

    public AttendanceDtos.Item checkAttendance(AttendanceDtos.CheckCommand command) {
        QRCode qrCode = qrCodeService.validate(new QRCodeDtos.ValidateCommand(command.hashValue())).qrCode();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(qrCode.getSessionId())).session();
        session.validateInProgressForAttendance();

        Member member = memberService.findById(new MemberDtos.FindByIdCommand(command.memberId())).member();
        member.validateCanAttend();

        CohortMember cohortMember = memberService.findCohortMember(new MemberDtos.FindCohortMemberCommand(member.getId(), session.getCohort().getId())).cohortMember();

        Attendance attendance = attendanceService.createByQr(new AttendanceDtos.CreateByQrCommand(session, cohortMember)).attendance();
        if (attendance.getPenaltyAmount() > 0) {
            depositService.deductPenalty(new DepositDtos.DeductPenaltyCommand(cohortMember, attendance.getPenaltyAmount(), "QR 출석 체크 패널티"));
        }
        return new AttendanceDtos.Item(attendance);
    }

    public AttendanceDtos.Item registerAttendance(AttendanceDtos.RegisterCommand command) {
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(command.sessionId())).session();
        Member member = memberService.findById(new MemberDtos.FindByIdCommand(command.memberId())).member();
        CohortMember cohortMember = memberService.findCohortMember(new MemberDtos.FindCohortMemberCommand(member.getId(), session.getCohort().getId())).cohortMember();

        Attendance attendance = attendanceService.createManual(new AttendanceDtos.CreateManualCommand(session, cohortMember, command.status())).attendance();
        if (attendance.getPenaltyAmount() > 0) {
            depositService.deductPenalty(new DepositDtos.DeductPenaltyCommand(cohortMember, attendance.getPenaltyAmount(), "관리자 출결 등록 패널티"));
        }
        return new AttendanceDtos.Item(attendance);
    }

    public AttendanceDtos.Item updateAttendance(AttendanceDtos.UpdateCommand command) {
        Attendance attendance = attendanceService.findById(new AttendanceDtos.FindByIdCommand(command.attendanceId())).attendance();
        CohortMember cohortMember = memberService.findCohortMemberById(new MemberDtos.FindCohortMemberByIdCommand(attendance.getCohortMemberId())).cohortMember();
        AttendanceStatus oldStatus = attendance.getStatus();

        int penaltyDiff = attendanceService.updateStatusAndGetPenaltyDiff(new AttendanceDtos.PenaltyDiffCommand(attendance, cohortMember, command.status())).value();
        try {
            if (penaltyDiff > 0) {
                depositService.deductPenalty(new DepositDtos.DeductPenaltyCommand(cohortMember, penaltyDiff, "출결 수정 추가 차감"));
            } else if (penaltyDiff < 0) {
                depositService.refund(new DepositDtos.RefundCommand(cohortMember, Math.abs(penaltyDiff), "출결 수정 환급"));
            }
        } catch (RuntimeException ex) {
            attendanceService.updateStatusAndGetPenaltyDiff(new AttendanceDtos.PenaltyDiffCommand(attendance, cohortMember, oldStatus));
            throw ex;
        }
        return new AttendanceDtos.Item(attendance);
    }
}
