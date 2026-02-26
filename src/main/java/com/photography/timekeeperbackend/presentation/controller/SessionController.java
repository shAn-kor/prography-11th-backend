package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.application.SessionFacade;
import com.photography.timekeeperbackend.application.SessionService;
import com.photography.timekeeperbackend.presentation.dto.session.SessionCreateRequest;
import com.photography.timekeeperbackend.presentation.dto.session.SessionResponse;
import com.photography.timekeeperbackend.presentation.dto.session.SessionStatusUpdateRequest;
import com.photography.timekeeperbackend.presentation.dto.session.SessionUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionFacade sessionFacade;

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionResponse>> getSessionsForMember() {
        return ResponseEntity.ok(sessionService.findAllForMember().sessions().stream().map(SessionResponse::from).toList());
    }

    @GetMapping("/admin/sessions")
    public ResponseEntity<List<SessionResponse>> getSessionsForAdmin() {
        return ResponseEntity.ok(sessionService.findAllForAdmin().sessions().stream().map(SessionResponse::from).toList());
    }

    @PostMapping("/admin/sessions")
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionResponse.from(
                sessionFacade.createSession(new SessionDtos.CreateFacadeCommand(
                        request.title(), request.description(), request.type(), request.sessionDate()
                )).session()
        ));
    }

    @PutMapping("/admin/sessions/{id}")
    public ResponseEntity<SessionResponse> updateSession(@PathVariable Long id, @Valid @RequestBody SessionUpdateRequest request) {
        return ResponseEntity.ok(SessionResponse.from(
                sessionService.update(new SessionDtos.UpdateCommand(
                        id, request.title(), request.description(), request.type(), request.sessionDate()
                )).session()
        ));
    }

    @DeleteMapping("/admin/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.delete(new SessionDtos.DeleteCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/sessions/{id}/status")
    public ResponseEntity<SessionResponse> updateSessionStatus(@PathVariable Long id,
                                                               @Valid @RequestBody SessionStatusUpdateRequest request) {
        return ResponseEntity.ok(SessionResponse.from(
                sessionService.updateStatus(new SessionDtos.UpdateStatusCommand(id, request.status())).session()
        ));
    }
}
