package com.shopwise.controller;

import com.shopwise.dto.PointTransactionDTO;
import com.shopwise.service.PointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PointTransactionController {

    private final PointTransactionService transactionService;

    @GetMapping("/client/{clientId}/transactions")
    public ResponseEntity<List<PointTransactionDTO>> getClientTransactions(@PathVariable Long clientId) {
        return ResponseEntity.ok(transactionService.getTransactionsByClient(clientId));
    }

    @GetMapping("/client/{clientId}/balance")
    public ResponseEntity<Map<String, Object>> getClientBalance(@PathVariable Long clientId) {
        Map<String, Object> response = new HashMap<>();
        response.put("clientId", clientId);
        response.put("balance", transactionService.getClientBalance(clientId));
        response.put("totalEarned", transactionService.getTotalEarnedPoints(clientId));
        response.put("totalRedeemed", transactionService.getTotalRedeemedPoints(clientId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<PointTransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/client/{clientId}/redeem")
    public ResponseEntity<PointTransactionDTO> redeemPoints(
            @PathVariable Long clientId,
            @RequestParam int points,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(transactionService.redeemPoints(clientId, points, description));
    }

    @PostMapping("/client/{clientId}/adjust")
    public ResponseEntity<PointTransactionDTO> adjustPoints(
            @PathVariable Long clientId,
            @RequestParam int points,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(transactionService.adjustPoints(clientId, points, description));
    }
}
