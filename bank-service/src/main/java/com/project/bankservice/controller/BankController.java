package com.project.bank_service.controller;

import com.project.bank_service.dto.request.CreateBankRequest;
import com.project.bank_service.dto.response.ApiResponse;
import com.project.bank_service.dto.response.BankResponse;
import com.project.bank_service.service.BankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
@Slf4j
public class BankController {

    private final BankService bankService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankResponse>> createBank(
            @Valid @RequestBody CreateBankRequest request) {
        log.info("Creating new bank: {}", request.getBankName());
        BankResponse bank = bankService.createBank(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bank, "Bank created successfully"));
    }

    @GetMapping("/{bankId}")
    public ResponseEntity<ApiResponse<BankResponse>> getBankById(
            @PathVariable String bankId) {  // Changed from UUID to String
        log.info("Fetching bank by ID: {}", bankId);
        BankResponse bank = bankService.getBankById(bankId);
        return ResponseEntity.ok(ApiResponse.success(bank, "Bank fetched successfully"));
    }

    @GetMapping("/code/{bankCode}")
    public ResponseEntity<ApiResponse<BankResponse>> getBankByCode(
            @PathVariable String bankCode) {
        log.info("Fetching bank by code: {}", bankCode);
        BankResponse bank = bankService.getBankByCode(bankCode);
        return ResponseEntity.ok(ApiResponse.success(bank, "Bank fetched successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> getAllBanks() {
        log.info("Fetching all banks");
        List<BankResponse> banks = bankService.getAllBanks();
        return ResponseEntity.ok(ApiResponse.success(banks, "Banks fetched successfully"));
    }

    @GetMapping("/upi-enabled")
    public ResponseEntity<ApiResponse<List<BankResponse>>> getUpiEnabledBanks() {
        log.info("Fetching UPI-enabled banks");
        List<BankResponse> banks = bankService.getUpiEnabledBanks();
        return ResponseEntity.ok(ApiResponse.success(banks, "UPI-enabled banks fetched successfully"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "bank-service"),
                "Service is healthy"));
    }
}