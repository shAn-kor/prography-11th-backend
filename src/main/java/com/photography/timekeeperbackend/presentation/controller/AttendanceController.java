package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.application.AttendanceFacade;
import com.photography.timekeeperbackend.application.AttendanceService;
import com.photography.timekeeperbackend.application.MemberService;
import com.photography.timekeeperbackend.application.SessionService;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.presentation.dto.attendance.AttendanceCheckRequest;
import com.photography.timekeeperbackend.presentation.dto.attendance.AttendanceCreateRequest;
import com.photography.timekeeperbackend.presentation.dto.attendance.AttendanceResponse;
import com.photography.timekeeperbackend.presentation.dto.attendance.AttendanceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceFacade attendanceFacade;
    private final AttendanceService attendanceService;
    private final MemberService memberService;
    private final SessionService sessionService;

    @PostMapping("/attendances")
    public ResponseEntity<AttendanceResponse> checkAttendance(@Valid @RequestBody AttendanceCheckRequest request) {
        Attendance attendance = attendanceFacade.checkAttendance(
                new AttendanceDtos.CheckCommand(request.hashValue(), request.memberId())
        ).attendance();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
        Member member = memberService.findMemberByCohortMemberId(
                new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
        ).member();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate())
        );
    }

    @GetMapping("/attendances")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendances(@RequestParam Long memberId) {
        return ResponseEntity.ok(attendanceService.findMyAttendances(new AttendanceDtos.FindMyCommand(memberId)).attendances().stream().map(attendance -> {
            Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
            Member member = memberService.findMemberByCohortMemberId(
                    new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
            ).member();
            return AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate());
        }).toList());
    }

    @GetMapping("/members/{id}/attendance-summary")
    public ResponseEntity<Map<AttendanceStatus, Long>> getAttendanceSummary(@PathVariable Long id) {
        Map<AttendanceStatus, Long> summary = attendanceService.findMyAttendances(new AttendanceDtos.FindMyCommand(id)).attendances().stream()
                .collect(java.util.stream.Collectors.groupingBy(Attendance::getStatus, java.util.stream.Collectors.counting()));
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/admin/attendances")
    public ResponseEntity<AttendanceResponse> registerAttendance(@Valid @RequestBody AttendanceCreateRequest request) {
        Attendance attendance = attendanceFacade.registerAttendance(
                new AttendanceDtos.RegisterCommand(request.sessionId(), request.memberId(), request.status())
        ).attendance();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
        Member member = memberService.findMemberByCohortMemberId(
                new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
        ).member();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate())
        );
    }

    @PutMapping("/admin/attendances/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(@PathVariable Long id,
                                                               @Valid @RequestBody AttendanceUpdateRequest request) {
        Attendance attendance = attendanceFacade.updateAttendance(new AttendanceDtos.UpdateCommand(id, request.status())).attendance();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
        Member member = memberService.findMemberByCohortMemberId(
                new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
        ).member();
        return ResponseEntity.ok(AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate()));
    }

    @GetMapping("/admin/attendances/sessions/{id}/summary")
    public ResponseEntity<Map<AttendanceStatus, Long>> getSessionSummary(@PathVariable Long id) {
        Map<AttendanceStatus, Long> summary = attendanceService.findBySession(new AttendanceDtos.FindBySessionCommand(id)).attendances().stream()
                .collect(java.util.stream.Collectors.groupingBy(Attendance::getStatus, java.util.stream.Collectors.counting()));
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/attendances/members/{id}")
    public ResponseEntity<List<AttendanceResponse>> getMemberAttendances(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.findMyAttendances(new AttendanceDtos.FindMyCommand(id)).attendances().stream().map(attendance -> {
            Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
            Member member = memberService.findMemberByCohortMemberId(
                    new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
            ).member();
            return AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate());
        }).toList());
    }

    @GetMapping("/admin/attendances/sessions/{id}")
    public ResponseEntity<List<AttendanceResponse>> getSessionAttendances(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.findBySession(new AttendanceDtos.FindBySessionCommand(id)).attendances().stream().map(attendance -> {
            Session session = sessionService.findById(new SessionDtos.FindByIdCommand(attendance.getSessionId())).session();
            Member member = memberService.findMemberByCohortMemberId(
                    new MemberDtos.FindMemberByCohortMemberIdCommand(attendance.getCohortMemberId())
            ).member();
            return AttendanceResponse.from(attendance, member.getLoginId(), session.getTitle(), session.getSessionDate());
        }).toList());
    }
}
