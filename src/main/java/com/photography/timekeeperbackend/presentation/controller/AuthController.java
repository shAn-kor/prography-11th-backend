package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.MemberDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.presentation.dto.auth.LoginRequest;
import com.photography.timekeeperbackend.presentation.dto.member.MemberResponse;
import com.photography.timekeeperbackend.application.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController



@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/auth/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(MemberResponse.from(
                memberService.login(new MemberDtos.LoginCommand(request.loginId(), request.password())).member()
        ));
    }
}
