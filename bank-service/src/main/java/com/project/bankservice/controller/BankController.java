package com.project.bank_service.controller;

import com.project.bank_service.dto.request.CreateBankRequest;
import com.project.bank_service.dto.response.ApiResponse;
import com.project.bank_service.dto.response.BankResponse;
import com.project.bank_service.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bank Management", description = "APIs for managing banks in the UPI Payment System")
public class BankController {

    private final BankService bankService;

    @Operation(
            summary = "Create a new bank",
            description = "Registers a new bank with UPI system. Bank code and IFSC prefix must be unique."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Bank created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Bank with same code or IFSC prefix already exists"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<BankResponse>> createBank(
            @Valid @RequestBody CreateBankRequest request) {
        log.info("Creating new bank: {}", request.getBankName());
        BankResponse bank = bankService.createBank(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bank, "Bank created successfully"));
    }

    @Operation(
            summary = "Get bank by ID",
            description = "Retrieves bank details by its unique ID"
    )
    @GetMapping("/{bankId}")
    public ResponseEntity<ApiResponse<BankResponse>> getBankById(
            @Parameter(description = "Bank ID", example = "BSBI001")
            @PathVariable String bankId) {
        log.info("Fetching bank by ID: {}", bankId);
        BankResponse bank = bankService.getBankById(bankId);
        return ResponseEntity.ok(ApiResponse.success(bank, "Bank fetched successfully"));
    }

    @Operation(
            summary = "Get bank by code",
            description = "Retrieves bank details by its bank code"
    )
    @GetMapping("/code/{bankCode}")
    public ResponseEntity<ApiResponse<BankResponse>> getBankByCode(
            @Parameter(description = "Bank code", example = "SBI")
            @PathVariable String bankCode) {
        log.info("Fetching bank by code: {}", bankCode);
        BankResponse bank = bankService.getBankByCode(bankCode);
        return ResponseEntity.ok(ApiResponse.success(bank, "Bank fetched successfully"));
    }

    @Operation(
            summary = "Get all banks",
            description = "Retrieves a list of all active banks"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> getAllBanks() {
        log.info("Fetching all banks");
        List<BankResponse> banks = bankService.getAllBanks();
        return ResponseEntity.ok(ApiResponse.success(banks, "Banks fetched successfully"));
    }

    @Operation(
            summary = "Get UPI-enabled banks",
            description = "Retrieves a list of all banks that support UPI transactions"
    )
    @GetMapping("/upi-enabled")
    public ResponseEntity<ApiResponse<List<BankResponse>>> getUpiEnabledBanks() {
        log.info("Fetching UPI-enabled banks");
        List<BankResponse> banks = bankService.getUpiEnabledBanks();
        return ResponseEntity.ok(ApiResponse.success(banks, "UPI-enabled banks fetched successfully"));
    }

    @Operation(
            summary = "Health check",
            description = "Returns the health status of the Bank Service"
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "bank-service"),
                "Service is healthy"));
    }
}