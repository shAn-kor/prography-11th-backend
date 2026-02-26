package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.DepositDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.presentation.dto.deposit.DepositHistoryResponse;
import com.photography.timekeeperbackend.application.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController



@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/admin/cohort-members/{id}/deposits")
    public ResponseEntity<List<DepositHistoryResponse>> getDepositHistory(@PathVariable Long id) {
        return ResponseEntity.ok(depositService.getHistory(new DepositDtos.GetHistoryCommand(id)).histories().stream()
                .map(DepositHistoryResponse::from).toList());
    }
}
