package com.project.bank_service.controller;

import com.project.bank_service.dto.request.LinkAccountRequest;
import com.project.bank_service.dto.response.ApiResponse;
import com.project.bank_service.dto.response.BankAccountResponse;
import com.project.bank_service.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bank Account Management", description = "APIs for managing user bank accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Operation(
            summary = "Link a bank account",
            description = "Links a user's bank account to their UPI profile. IFSC must match the bank's prefix."
    )
    @PostMapping("/link")
    public ResponseEntity<ApiResponse<BankAccountResponse>> linkAccount(
            @Valid @RequestBody LinkAccountRequest request) {
        log.info("Linking account for user: {}", request.getUserId());
        BankAccountResponse account = bankAccountService.linkAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(account, "Account linked successfully"));
    }

    @Operation(
            summary = "Get account by ID",
            description = "Retrieves bank account details by account ID"
    )
    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getAccountById(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId) {
        log.info("Fetching account by ID: {}", accountId);
        BankAccountResponse account = bankAccountService.getAccountById(accountId);
        return ResponseEntity.ok(ApiResponse.success(account, "Account fetched successfully"));
    }

    @Operation(
            summary = "Get user's accounts",
            description = "Retrieves all bank accounts linked to a user"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BankAccountResponse>>> getAccountsByUserId(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Fetching accounts for user: {}", userId);
        List<BankAccountResponse> accounts = bankAccountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(accounts, "Accounts fetched successfully"));
    }

    @Operation(
            summary = "Get primary account",
            description = "Retrieves the primary bank account for a user"
    )
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getPrimaryAccount(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Fetching primary account for user: {}", userId);
        BankAccountResponse account = bankAccountService.getPrimaryAccount(userId);
        return ResponseEntity.ok(ApiResponse.success(account, "Primary account fetched successfully"));
    }

    @Operation(
            summary = "Set primary account",
            description = "Sets a bank account as the user's primary account"
    )
    @PutMapping("/{accountId}/set-primary")
    public ResponseEntity<ApiResponse<Void>> setPrimaryAccount(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId,
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        log.info("Setting account {} as primary for user: {}", accountId, userId);
        bankAccountService.setPrimaryAccount(userId, accountId);
        return ResponseEntity.ok(ApiResponse.success("Account set as primary successfully"));
    }

    @Operation(
            summary = "Get account balance",
            description = "Retrieves the current balance of a bank account"
    )
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getBalance(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId) {
        log.info("Fetching balance for account: {}", accountId);
        BigDecimal balance = bankAccountService.getBalance(accountId);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("balance", balance),
                "Balance fetched successfully"));
    }

    @Operation(
            summary = "Credit amount",
            description = "Credits (adds) money to a bank account"
    )
    @PostMapping("/{accountId}/credit")
    public ResponseEntity<ApiResponse<Void>> creditAccount(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        log.info("Crediting {} to account: {}", amount, accountId);
        bankAccountService.creditAccount(accountId, amount);
        return ResponseEntity.ok(ApiResponse.success("Amount credited successfully"));
    }

    @Operation(
            summary = "Debit amount",
            description = "Debits (deducts) money from a bank account"
    )
    @PostMapping("/{accountId}/debit")
    public ResponseEntity<ApiResponse<Void>> debitAccount(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        log.info("Debiting {} from account: {}", amount, accountId);
        bankAccountService.debitAccount(accountId, amount);
        return ResponseEntity.ok(ApiResponse.success("Amount debited successfully"));
    }

    @Operation(
            summary = "Verify account",
            description = "Marks a bank account as verified"
    )
    @PutMapping("/{accountId}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId) {
        log.info("Verifying account: {}", accountId);
        bankAccountService.verifyAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success("Account verified successfully"));
    }

    @Operation(
            summary = "Deactivate account",
            description = "Deactivates (soft deletes) a bank account"
    )
    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @Parameter(description = "Account ID", example = "A100001SBISAV")
            @PathVariable String accountId) {
        log.info("Deactivating account: {}", accountId);
        bankAccountService.deactivateAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully"));
    }
}