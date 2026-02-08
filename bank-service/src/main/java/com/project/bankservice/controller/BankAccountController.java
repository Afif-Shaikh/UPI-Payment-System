package com.project.bank_service.controller;

import com.project.bank_service.dto.request.LinkAccountRequest;
import com.project.bank_service.dto.response.ApiResponse;
import com.project.bank_service.dto.response.BankAccountResponse;
import com.project.bank_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/link")
    public ResponseEntity<ApiResponse<BankAccountResponse>> linkAccount(
            @Valid @RequestBody LinkAccountRequest request) {
        log.info("Linking account for user: {}", request.getUserId());
        BankAccountResponse account = bankAccountService.linkAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(account, "Account linked successfully"));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getAccountById(
            @PathVariable String accountId) {  // Changed from UUID to String
        log.info("Fetching account by ID: {}", accountId);
        BankAccountResponse account = bankAccountService.getAccountById(accountId);
        return ResponseEntity.ok(ApiResponse.success(account, "Account fetched successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BankAccountResponse>>> getAccountsByUserId(
            @PathVariable String userId) {  // Changed from UUID to String
        log.info("Fetching accounts for user: {}", userId);
        List<BankAccountResponse> accounts = bankAccountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(accounts, "Accounts fetched successfully"));
    }

    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getPrimaryAccount(
            @PathVariable String userId) {  // Changed from UUID to String
        log.info("Fetching primary account for user: {}", userId);
        BankAccountResponse account = bankAccountService.getPrimaryAccount(userId);
        return ResponseEntity.ok(ApiResponse.success(account, "Primary account fetched successfully"));
    }

    @PutMapping("/{accountId}/set-primary")
    public ResponseEntity<ApiResponse<Void>> setPrimaryAccount(
            @PathVariable String accountId,  // Changed from UUID to String
            @RequestBody Map<String, String> request) {  // Changed from UUID to String
        String userId = request.get("userId");
        log.info("Setting account {} as primary for user: {}", accountId, userId);
        bankAccountService.setPrimaryAccount(userId, accountId);
        return ResponseEntity.ok(ApiResponse.success("Account set as primary successfully"));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getBalance(
            @PathVariable String accountId) {  // Changed from UUID to String
        log.info("Fetching balance for account: {}", accountId);
        BigDecimal balance = bankAccountService.getBalance(accountId);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("balance", balance),
                "Balance fetched successfully"));
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<ApiResponse<Void>> creditAccount(
            @PathVariable String accountId,  // Changed from UUID to String
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        log.info("Crediting {} to account: {}", amount, accountId);
        bankAccountService.creditAccount(accountId, amount);
        return ResponseEntity.ok(ApiResponse.success("Amount credited successfully"));
    }

    @PostMapping("/{accountId}/debit")
    public ResponseEntity<ApiResponse<Void>> debitAccount(
            @PathVariable String accountId,  // Changed from UUID to String
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        log.info("Debiting {} from account: {}", amount, accountId);
        bankAccountService.debitAccount(accountId, amount);
        return ResponseEntity.ok(ApiResponse.success("Amount debited successfully"));
    }

    @PutMapping("/{accountId}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(
            @PathVariable String accountId) {  // Changed from UUID to String
        log.info("Verifying account: {}", accountId);
        bankAccountService.verifyAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success("Account verified successfully"));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @PathVariable String accountId) {  // Changed from UUID to String
        log.info("Deactivating account: {}", accountId);
        bankAccountService.deactivateAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully"));
    }
}