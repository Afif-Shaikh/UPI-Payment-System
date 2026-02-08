package com.project.vpa_service.controller;

import com.project.vpa_service.dto.request.CreateVpaRequest;
import com.project.vpa_service.dto.request.VerifyVpaRequest;
import com.project.vpa_service.dto.response.ApiResponse;
import com.project.vpa_service.dto.response.VpaResponse;
import com.project.vpa_service.dto.response.VpaVerificationResponse;
import com.project.vpa_service.service.VpaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vpas")
@RequiredArgsConstructor
@Slf4j
public class VpaController {

    private final VpaService vpaService;

    /**
     * Create a new VPA
     * POST /api/vpas
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VpaResponse>> createVpa(
            @Valid @RequestBody CreateVpaRequest request) {
        log.info("Creating VPA for user: {}", request.getUserId());
        VpaResponse vpa = vpaService.createVpa(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(vpa, "VPA created successfully"));
    }

    /**
     * Get VPA by ID
     * GET /api/vpas/{vpaId}
     */
    @GetMapping("/{vpaId}")
    public ResponseEntity<ApiResponse<VpaResponse>> getVpaById(
            @PathVariable String vpaId) {
        log.info("Fetching VPA by ID: {}", vpaId);
        VpaResponse vpa = vpaService.getVpaById(vpaId);
        return ResponseEntity.ok(ApiResponse.success(vpa, "VPA fetched successfully"));
    }

    /**
     * Get VPA by address
     * GET /api/vpas/address/{vpaAddress}
     */
    @GetMapping("/address/{vpaAddress}")
    public ResponseEntity<ApiResponse<VpaResponse>> getVpaByAddress(
            @PathVariable String vpaAddress) {
        log.info("Fetching VPA by address: {}", vpaAddress);
        VpaResponse vpa = vpaService.getVpaByAddress(vpaAddress);
        return ResponseEntity.ok(ApiResponse.success(vpa, "VPA fetched successfully"));
    }

    /**
     * Get all VPAs for a user
     * GET /api/vpas/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VpaResponse>>> getVpasByUserId(
            @PathVariable String userId) {
        log.info("Fetching VPAs for user: {}", userId);
        List<VpaResponse> vpas = vpaService.getVpasByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(vpas, "VPAs fetched successfully"));
    }

    /**
     * Get primary VPA for a user
     * GET /api/vpas/user/{userId}/primary
     */
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ApiResponse<VpaResponse>> getPrimaryVpa(
            @PathVariable String userId) {
        log.info("Fetching primary VPA for user: {}", userId);
        VpaResponse vpa = vpaService.getPrimaryVpa(userId);
        return ResponseEntity.ok(ApiResponse.success(vpa, "Primary VPA fetched successfully"));
    }

    /**
     * Verify VPA (check if exists and active)
     * POST /api/vpas/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VpaVerificationResponse>> verifyVpa(
            @Valid @RequestBody VerifyVpaRequest request) {
        log.info("Verifying VPA: {}", request.getVpaAddress());
        VpaVerificationResponse result = vpaService.verifyVpa(request.getVpaAddress());
        String message = result.getExists() ? "VPA exists and is active" : "VPA does not exist";
        return ResponseEntity.ok(ApiResponse.success(result, message));
    }

    /**
     * Check if VPA is available
     * GET /api/vpas/check-availability/{vpaAddress}
     */
    @GetMapping("/check-availability/{vpaAddress}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkVpaAvailability(
            @PathVariable String vpaAddress) {
        log.info("Checking VPA availability: {}", vpaAddress);
        boolean available = vpaService.isVpaAvailable(vpaAddress);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("available", available),
                available ? "VPA is available" : "VPA is already taken"));
    }

    /**
     * Set VPA as primary
     * PUT /api/vpas/{vpaId}/set-primary
     */
    @PutMapping("/{vpaId}/set-primary")
    public ResponseEntity<ApiResponse<Void>> setPrimaryVpa(
            @PathVariable String vpaId,
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        log.info("Setting VPA {} as primary for user: {}", vpaId, userId);
        vpaService.setPrimaryVpa(userId, vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA set as primary successfully"));
    }

    /**
     * Update linked account
     * PUT /api/vpas/{vpaId}/link-account
     */
    @PutMapping("/{vpaId}/link-account")
    public ResponseEntity<ApiResponse<Void>> updateLinkedAccount(
            @PathVariable String vpaId,
            @RequestBody Map<String, String> request) {
        String accountId = request.get("accountId");
        log.info("Updating linked account for VPA {}: {}", vpaId, accountId);
        vpaService.updateLinkedAccount(vpaId, accountId);
        return ResponseEntity.ok(ApiResponse.success("Linked account updated successfully"));
    }

    /**
     * Mark VPA as verified
     * PUT /api/vpas/{vpaId}/verify
     */
    @PutMapping("/{vpaId}/verify")
    public ResponseEntity<ApiResponse<Void>> markVpaAsVerified(
            @PathVariable String vpaId) {
        log.info("Marking VPA as verified: {}", vpaId);
        vpaService.markVpaAsVerified(vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA verified successfully"));
    }

    /**
     * Deactivate VPA
     * DELETE /api/vpas/{vpaId}
     */
    @DeleteMapping("/{vpaId}")
    public ResponseEntity<ApiResponse<Void>> deactivateVpa(
            @PathVariable String vpaId) {
        log.info("Deactivating VPA: {}", vpaId);
        vpaService.deactivateVpa(vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA deactivated successfully"));
    }

    /**
     * Health check
     * GET /api/vpas/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "vpa-service"),
                "Service is healthy"));
    }
}