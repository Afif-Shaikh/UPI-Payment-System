package com.project.vpa_service.controller;

import com.project.vpa_service.dto.request.CreateVpaRequest;
import com.project.vpa_service.dto.request.VerifyVpaRequest;
import com.project.vpa_service.dto.response.ApiResponse;
import com.project.vpa_service.dto.response.VpaResponse;
import com.project.vpa_service.dto.response.VpaVerificationResponse;
import com.project.vpa_service.service.VpaService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/vpas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "VPA Management", description = "APIs for managing Virtual Payment Addresses (UPI IDs)")
public class VpaController {

    private final VpaService vpaService;

    @Operation(
            summary = "Health check",
            description = "Returns the health status of the VPA Service"
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "vpa-service"),
                "Service is healthy"));
    }

    @Operation(
            summary = "Create a new VPA",
            description = "Creates a new Virtual Payment Address (UPI ID) for a user"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<VpaResponse>> createVpa(
            @Valid @RequestBody CreateVpaRequest request) {
        log.info("Creating VPA for user: {}", request.getUserId());
        VpaResponse vpa = vpaService.createVpa(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(vpa, "VPA created successfully"));
    }

    @Operation(
            summary = "Verify VPA",
            description = "Checks if a VPA exists and is active. Returns masked account holder name."
    )
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VpaVerificationResponse>> verifyVpa(
            @Valid @RequestBody VerifyVpaRequest request) {
        log.info("Verifying VPA: {}", request.getVpaAddress());
        VpaVerificationResponse result = vpaService.verifyVpa(request.getVpaAddress());
        String message = result.getExists() ? "VPA exists and is active" : "VPA does not exist";
        return ResponseEntity.ok(ApiResponse.success(result, message));
    }

    @Operation(
            summary = "Check VPA availability",
            description = "Checks if a VPA address is available for registration"
    )
    @GetMapping("/check-availability/{vpaAddress}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkVpaAvailability(
            @Parameter(description = "VPA address to check", example = "rahul@okaxis")
            @PathVariable String vpaAddress) {
        log.info("Checking VPA availability: {}", vpaAddress);
        boolean available = vpaService.isVpaAvailable(vpaAddress);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("available", available),
                available ? "VPA is available" : "VPA is already taken"));
    }

    @Operation(
            summary = "Get VPA by address",
            description = "Retrieves VPA details by its address"
    )
    @GetMapping("/address/{vpaAddress}")
    public ResponseEntity<ApiResponse<VpaResponse>> getVpaByAddress(
            @Parameter(description = "VPA address", example = "rahul@okaxis")
            @PathVariable String vpaAddress) {
        log.info("Fetching VPA by address: {}", vpaAddress);
        VpaResponse vpa = vpaService.getVpaByAddress(vpaAddress);
        return ResponseEntity.ok(ApiResponse.success(vpa, "VPA fetched successfully"));
    }

    @Operation(
            summary = "Get user's VPAs",
            description = "Retrieves all VPAs belonging to a user"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VpaResponse>>> getVpasByUserId(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Fetching VPAs for user: {}", userId);
        List<VpaResponse> vpas = vpaService.getVpasByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(vpas, "VPAs fetched successfully"));
    }

    @Operation(
            summary = "Get primary VPA",
            description = "Retrieves the primary VPA for a user"
    )
    @GetMapping("/user/{userId}/primary")
    public ResponseEntity<ApiResponse<VpaResponse>> getPrimaryVpa(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Fetching primary VPA for user: {}", userId);
        VpaResponse vpa = vpaService.getPrimaryVpa(userId);
        return ResponseEntity.ok(ApiResponse.success(vpa, "Primary VPA fetched successfully"));
    }

    @Operation(
            summary = "Get VPA by ID",
            description = "Retrieves VPA details by its ID"
    )
    @GetMapping("/{vpaId}")
    public ResponseEntity<ApiResponse<VpaResponse>> getVpaById(
            @Parameter(description = "VPA ID", example = "VPA100001")
            @PathVariable String vpaId) {
        log.info("Fetching VPA by ID: {}", vpaId);
        VpaResponse vpa = vpaService.getVpaById(vpaId);
        return ResponseEntity.ok(ApiResponse.success(vpa, "VPA fetched successfully"));
    }

    @Operation(
            summary = "Set primary VPA",
            description = "Sets a VPA as the user's primary VPA"
    )
    @PutMapping("/{vpaId}/set-primary")
    public ResponseEntity<ApiResponse<Void>> setPrimaryVpa(
            @Parameter(description = "VPA ID", example = "VPA100001")
            @PathVariable String vpaId,
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        log.info("Setting VPA {} as primary for user: {}", vpaId, userId);
        vpaService.setPrimaryVpa(userId, vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA set as primary successfully"));
    }

    @Operation(
            summary = "Update linked account",
            description = "Updates the bank account linked to a VPA"
    )
    @PutMapping("/{vpaId}/link-account")
    public ResponseEntity<ApiResponse<Void>> updateLinkedAccount(
            @Parameter(description = "VPA ID", example = "VPA100001")
            @PathVariable String vpaId,
            @RequestBody Map<String, String> request) {
        String accountId = request.get("accountId");
        log.info("Updating linked account for VPA {}: {}", vpaId, accountId);
        vpaService.updateLinkedAccount(vpaId, accountId);
        return ResponseEntity.ok(ApiResponse.success("Linked account updated successfully"));
    }

    @Operation(
            summary = "Mark VPA as verified",
            description = "Marks a VPA as verified after validation"
    )
    @PutMapping("/{vpaId}/mark-verified")
    public ResponseEntity<ApiResponse<Void>> markVpaAsVerified(
            @Parameter(description = "VPA ID", example = "VPA100001")
            @PathVariable String vpaId) {
        log.info("Marking VPA as verified: {}", vpaId);
        vpaService.markVpaAsVerified(vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA verified successfully"));
    }

    @Operation(
            summary = "Deactivate VPA",
            description = "Deactivates (soft deletes) a VPA"
    )
    @DeleteMapping("/{vpaId}")
    public ResponseEntity<ApiResponse<Void>> deactivateVpa(
            @Parameter(description = "VPA ID", example = "VPA100001")
            @PathVariable String vpaId) {
        log.info("Deactivating VPA: {}", vpaId);
        vpaService.deactivateVpa(vpaId);
        return ResponseEntity.ok(ApiResponse.success("VPA deactivated successfully"));
    }
}