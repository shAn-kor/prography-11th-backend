package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.MemberDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.application.MemberFacade;
import com.photography.timekeeperbackend.application.MemberService;
import com.photography.timekeeperbackend.presentation.dto.member.MemberCreateRequest;
import com.photography.timekeeperbackend.presentation.dto.member.MemberResponse;
import com.photography.timekeeperbackend.presentation.dto.member.MemberUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberFacade memberFacade;
    private final MemberService memberService;

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(MemberResponse.from(memberService.findById(new MemberDtos.FindByIdCommand(id)).member()));
    }

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberResponse>> getMembersDashboard() {
        return ResponseEntity.ok(memberService.findAll().members().stream().map(MemberResponse::from).toList());
    }

    @GetMapping("/admin/members/{id}")
    public ResponseEntity<MemberResponse> getMemberDetail(@PathVariable Long id) {
        return ResponseEntity.ok(MemberResponse.from(memberService.findById(new MemberDtos.FindByIdCommand(id)).member()));
    }

    @PostMapping("/admin/members")
    public ResponseEntity<MemberResponse> registerMember(@Valid @RequestBody MemberCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(
                memberFacade.register(new MemberDtos.RegisterCommand(
                        request.loginId(), request.password(), request.name(), request.partId(), request.teamId()
                )).member()
        ));
    }

    @PutMapping("/admin/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberUpdateRequest request) {
        return ResponseEntity.ok(MemberResponse.from(memberService.updateMember(new MemberDtos.UpdateCommand(id, request.name())).member()));
    }

    @DeleteMapping("/admin/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.withdrawMember(new MemberDtos.WithdrawCommand(id));
        return ResponseEntity.noContent().build();
    }
}
