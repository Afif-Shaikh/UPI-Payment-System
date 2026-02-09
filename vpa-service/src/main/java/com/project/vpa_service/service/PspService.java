package com.project.vpa_service.service;

import com.project.vpa_service.dto.request.CreatePspRequest;
import com.project.vpa_service.dto.response.PspResponse;
import com.project.vpa_service.entity.Psp;
import com.project.vpa_service.exception.PspNotFoundException;
import com.project.vpa_service.repository.PspRepository;
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
public class PspService {

    private final PspRepository pspRepository;
    private final IdGeneratorService idGeneratorService;

    /**
     * Create a new PSP
     */
    public PspResponse createPsp(CreatePspRequest request) {
        log.info("Creating new PSP: {}", request.getPspName());

        if (pspRepository.existsByPspHandle(request.getPspHandle().toLowerCase())) {
            throw new IllegalArgumentException("PSP with handle '" + request.getPspHandle() + "' already exists");
        }

        String pspId = idGeneratorService.generatePspId();

        Psp psp = Psp.builder()
                .id(pspId)
                .pspName(request.getPspName().trim())
                .pspHandle(request.getPspHandle().toLowerCase().trim())
                .bankName(request.getBankName())
                .bankIfscPrefix(request.getBankIfscPrefix())
                .logoUrl(request.getLogoUrl())
                .active(true)
                .build();

        psp = pspRepository.save(psp);
        log.info("PSP created successfully with ID: {}", psp.getId());

        return mapToPspResponse(psp);
    }

    /**
     * Get PSP by ID
     */
    @Transactional(readOnly = true)
    public PspResponse getPspById(String pspId) {
        log.info("Fetching PSP by ID: {}", pspId);
        Psp psp = pspRepository.findByIdAndActiveTrue(pspId)
                .orElseThrow(() -> new PspNotFoundException("id", pspId));
        return mapToPspResponse(psp);
    }

    /**
     * Get PSP by handle
     */
    @Transactional(readOnly = true)
    public PspResponse getPspByHandle(String pspHandle) {
        log.info("Fetching PSP by handle: {}", pspHandle);
        Psp psp = pspRepository.findByPspHandleAndActiveTrue(pspHandle.toLowerCase())
                .orElseThrow(() -> new PspNotFoundException("handle", pspHandle));
        return mapToPspResponse(psp);
    }

    /**
     * Get all PSPs
     */
    @Transactional(readOnly = true)
    public List<PspResponse> getAllPsps() {
        log.info("Fetching all active PSPs");
        return pspRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToPspResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get PSP entity by ID (internal use)
     */
    @Transactional(readOnly = true)
    public Psp getPspEntityById(String pspId) {
        return pspRepository.findByIdAndActiveTrue(pspId)
                .orElseThrow(() -> new PspNotFoundException("id", pspId));
    }

    private PspResponse mapToPspResponse(Psp psp) {
        return PspResponse.builder()
                .id(psp.getId())
                .pspName(psp.getPspName())
                .pspHandle(psp.getPspHandle())
                .bankName(psp.getBankName())
                .bankIfscPrefix(psp.getBankIfscPrefix())
                .logoUrl(psp.getLogoUrl())
                .active(psp.getActive())
                .createdAt(psp.getCreatedAt())
                .build();
    }
}