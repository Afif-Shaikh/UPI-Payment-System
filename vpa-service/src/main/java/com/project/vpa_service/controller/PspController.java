package com.project.vpa_service.controller;

import com.project.vpa_service.dto.request.CreatePspRequest;
import com.project.vpa_service.dto.response.ApiResponse;
import com.project.vpa_service.dto.response.PspResponse;
import com.project.vpa_service.service.PspService;
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
public class PspController {

    private final PspService pspService;

    /**
     * Create a new PSP
     * POST /api/psps
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PspResponse>> createPsp(
            @Valid @RequestBody CreatePspRequest request) {
        log.info("Creating new PSP: {}", request.getPspName());
        PspResponse psp = pspService.createPsp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(psp, "PSP created successfully"));
    }

    /**
     * Get PSP by ID
     * GET /api/psps/{pspId}
     */
    @GetMapping("/{pspId}")
    public ResponseEntity<ApiResponse<PspResponse>> getPspById(
            @PathVariable String pspId) {
        log.info("Fetching PSP by ID: {}", pspId);
        PspResponse psp = pspService.getPspById(pspId);
        return ResponseEntity.ok(ApiResponse.success(psp, "PSP fetched successfully"));
    }

    /**
     * Get PSP by handle
     * GET /api/psps/handle/{pspHandle}
     */
    @GetMapping("/handle/{pspHandle}")
    public ResponseEntity<ApiResponse<PspResponse>> getPspByHandle(
            @PathVariable String pspHandle) {
        log.info("Fetching PSP by handle: {}", pspHandle);
        PspResponse psp = pspService.getPspByHandle(pspHandle);
        return ResponseEntity.ok(ApiResponse.success(psp, "PSP fetched successfully"));
    }

    /**
     * Get all PSPs
     * GET /api/psps
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PspResponse>>> getAllPsps() {
        log.info("Fetching all PSPs");
        List<PspResponse> psps = pspService.getAllPsps();
        return ResponseEntity.ok(ApiResponse.success(psps, "PSPs fetched successfully"));
    }
}