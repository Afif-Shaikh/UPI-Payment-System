package com.project.vpa_service.controller;

import com.project.vpa_service.dto.request.CreatePspRequest;
import com.project.vpa_service.dto.response.ApiResponse;
import com.project.vpa_service.dto.response.PspResponse;
import com.project.vpa_service.service.PspService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psps")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "PSP Management", description = "APIs for managing Payment Service Providers")
public class PspController {

    private final PspService pspService;

    @Operation(
            summary = "Create a new PSP",
            description = "Registers a new Payment Service Provider (e.g., Google Pay, PhonePe)"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PspResponse>> createPsp(
            @Valid @RequestBody CreatePspRequest request) {
        log.info("Creating new PSP: {}", request.getPspName());
        PspResponse psp = pspService.createPsp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(psp, "PSP created successfully"));
    }

    @Operation(
            summary = "Get PSP by ID",
            description = "Retrieves PSP details by its ID"
    )
    @GetMapping("/{pspId}")
    public ResponseEntity<ApiResponse<PspResponse>> getPspById(
            @Parameter(description = "PSP ID", example = "PSP001")
            @PathVariable String pspId) {
        log.info("Fetching PSP by ID: {}", pspId);
        PspResponse psp = pspService.getPspById(pspId);
        return ResponseEntity.ok(ApiResponse.success(psp, "PSP fetched successfully"));
    }

    @Operation(
            summary = "Get PSP by handle",
            description = "Retrieves PSP details by its handle (e.g., okaxis, ybl)"
    )
    @GetMapping("/handle/{pspHandle}")
    public ResponseEntity<ApiResponse<PspResponse>> getPspByHandle(
            @Parameter(description = "PSP handle", example = "okaxis")
            @PathVariable String pspHandle) {
        log.info("Fetching PSP by handle: {}", pspHandle);
        PspResponse psp = pspService.getPspByHandle(pspHandle);
        return ResponseEntity.ok(ApiResponse.success(psp, "PSP fetched successfully"));
    }

    @Operation(
            summary = "Get all PSPs",
            description = "Retrieves a list of all active Payment Service Providers"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<PspResponse>>> getAllPsps() {
        log.info("Fetching all PSPs");
        List<PspResponse> psps = pspService.getAllPsps();
        return ResponseEntity.ok(ApiResponse.success(psps, "PSPs fetched successfully"));
    }
}