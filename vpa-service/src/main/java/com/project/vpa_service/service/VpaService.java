package com.project.vpa_service.service;

import com.project.vpa_service.dto.request.CreateVpaRequest;
import com.project.vpa_service.dto.response.VpaResponse;
import com.project.vpa_service.dto.response.VpaVerificationResponse;
import com.project.vpa_service.entity.Psp;
import com.project.vpa_service.entity.Vpa;
import com.project.vpa_service.exception.DuplicateVpaException;
import com.project.vpa_service.exception.VpaNotFoundException;
import com.project.vpa_service.repository.VpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VpaService {

    private final VpaRepository vpaRepository;
    private final PspService pspService;
    private final IdGeneratorService idGeneratorService;

    /**
     * Create a new VPA
     */
    public VpaResponse createVpa(CreateVpaRequest request) {
        log.info("Creating VPA for user: {}", request.getUserId());

        // Get PSP
        Psp psp = pspService.getPspEntityById(request.getPspId());

        // Generate VPA address
        String vpaAddress = request.getVpaHandle().toLowerCase() + "@" + psp.getPspHandle();

        // Check if VPA address already exists
        if (vpaRepository.existsByVpaAddress(vpaAddress)) {
            throw new DuplicateVpaException(vpaAddress);
        }

        // If this should be primary or first VPA, clear existing primary
        if (request.getIsPrimary() || vpaRepository.countByUserIdAndActiveTrue(request.getUserId()) == 0) {
            vpaRepository.clearPrimaryVpa(request.getUserId());
        }

        // Generate VPA ID
        String vpaId = idGeneratorService.generateVpaId();

        Vpa vpa = Vpa.builder()
                .id(vpaId)
                .userId(request.getUserId())
                .vpaHandle(request.getVpaHandle().toLowerCase())
                .psp(psp)
                .vpaAddress(vpaAddress)
                .linkedAccountId(request.getLinkedAccountId())
                .isPrimary(request.getIsPrimary() || vpaRepository.countByUserIdAndActiveTrue(request.getUserId()) == 0)
                .isVerified(false)
                .active(true)
                .build();

        vpa = vpaRepository.save(vpa);
        log.info("VPA created successfully: {}", vpa.getVpaAddress());

        return mapToVpaResponse(vpa);
    }

    /**
     * Get VPA by ID
     */
    @Transactional(readOnly = true)
    public VpaResponse getVpaById(String vpaId) {
        log.info("Fetching VPA by ID: {}", vpaId);
        Vpa vpa = vpaRepository.findByIdAndActiveTrue(vpaId)
                .orElseThrow(() -> new VpaNotFoundException("id", vpaId));
        return mapToVpaResponse(vpa);
    }

    /**
     * Get VPA by address (e.g., rahul@okaxis)
     */
    @Transactional(readOnly = true)
    public VpaResponse getVpaByAddress(String vpaAddress) {
        log.info("Fetching VPA by address: {}", vpaAddress);
        Vpa vpa = vpaRepository.findByVpaAddressAndActiveTrue(vpaAddress.toLowerCase())
                .orElseThrow(() -> new VpaNotFoundException("address", vpaAddress));
        return mapToVpaResponse(vpa);
    }

    /**
     * Get all VPAs for a user
     */
    @Transactional(readOnly = true)
    public List<VpaResponse> getVpasByUserId(String userId) {
        log.info("Fetching VPAs for user: {}", userId);
        return vpaRepository.findAllByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::mapToVpaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get primary VPA for a user
     */
    @Transactional(readOnly = true)
    public VpaResponse getPrimaryVpa(String userId) {
        log.info("Fetching primary VPA for user: {}", userId);
        Vpa vpa = vpaRepository.findByUserIdAndIsPrimaryTrueAndActiveTrue(userId)
                .orElseThrow(() -> new VpaNotFoundException("userId (primary)", userId));
        return mapToVpaResponse(vpa);
    }

    /**
     * Verify if VPA exists and is active
     */
    @Transactional(readOnly = true)
    public VpaVerificationResponse verifyVpa(String vpaAddress) {
        log.info("Verifying VPA: {}", vpaAddress);

        return vpaRepository.findByVpaAddressAndActiveTrue(vpaAddress.toLowerCase())
                .map(vpa -> VpaVerificationResponse.builder()
                        .vpaAddress(vpa.getVpaAddress())
                        .exists(true)
                        .active(vpa.getActive())
                        .accountHolderName(VpaVerificationResponse.maskName("Account Holder"))  // In real app, fetch from user-service
                        .pspName(vpa.getPsp().getPspName())
                        .build())
                .orElse(VpaVerificationResponse.builder()
                        .vpaAddress(vpaAddress)
                        .exists(false)
                        .active(false)
                        .build());
    }

    /**
     * Check if VPA address is available
     */
    @Transactional(readOnly = true)
    public boolean isVpaAvailable(String vpaAddress) {
        log.info("Checking if VPA is available: {}", vpaAddress);
        return !vpaRepository.existsByVpaAddress(vpaAddress.toLowerCase());
    }

    /**
     * Set VPA as primary
     */
    public void setPrimaryVpa(String userId, String vpaId) {
        log.info("Setting VPA {} as primary for user: {}", vpaId, userId);

        Vpa vpa = vpaRepository.findByIdAndActiveTrue(vpaId)
                .orElseThrow(() -> new VpaNotFoundException("id", vpaId));

        if (!vpa.getUserId().equals(userId)) {
            throw new IllegalArgumentException("VPA does not belong to the user");
        }

        vpaRepository.clearPrimaryVpa(userId);
        vpaRepository.setPrimaryVpa(vpaId);

        log.info("Primary VPA updated successfully");
    }

    /**
     * Update linked account for VPA
     */
    public void updateLinkedAccount(String vpaId, String accountId) {
        log.info("Updating linked account for VPA {}: {}", vpaId, accountId);

        if (!vpaRepository.existsById(vpaId)) {
            throw new VpaNotFoundException("id", vpaId);
        }

        vpaRepository.updateLinkedAccount(vpaId, accountId);
        log.info("Linked account updated successfully");
    }

    /**
     * Verify VPA (mark as verified)
     */
    public void markVpaAsVerified(String vpaId) {
        log.info("Marking VPA as verified: {}", vpaId);

        if (!vpaRepository.existsById(vpaId)) {
            throw new VpaNotFoundException("id", vpaId);
        }

        vpaRepository.verifyVpa(vpaId);
        log.info("VPA verified successfully");
    }

    /**
     * Deactivate VPA
     */
    public void deactivateVpa(String vpaId) {
        log.info("Deactivating VPA: {}", vpaId);

        if (!vpaRepository.existsById(vpaId)) {
            throw new VpaNotFoundException("id", vpaId);
        }

        vpaRepository.deactivateVpa(vpaId);
        log.info("VPA deactivated successfully");
    }

    private VpaResponse mapToVpaResponse(Vpa vpa) {
        return VpaResponse.builder()
                .id(vpa.getId())
                .userId(vpa.getUserId())
                .vpaHandle(vpa.getVpaHandle())
                .vpaAddress(vpa.getVpaAddress())
                .pspId(vpa.getPsp().getId())
                .pspName(vpa.getPsp().getPspName())
                .pspHandle(vpa.getPsp().getPspHandle())
                .linkedAccountId(vpa.getLinkedAccountId())
                .isPrimary(vpa.getIsPrimary())
                .isVerified(vpa.getIsVerified())
                .active(vpa.getActive())
                .createdAt(vpa.getCreatedAt())
                .build();
    }
}