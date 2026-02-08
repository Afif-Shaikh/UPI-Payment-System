package com.project.bank_service.service;

import com.project.bank_service.dto.request.CreateBankRequest;
import com.project.bank_service.dto.response.BankResponse;
import com.project.bank_service.entity.Bank;
import com.project.bank_service.exception.BankNotFoundException;
import com.project.bank_service.exception.DuplicateResourceException;
import com.project.bank_service.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankService {

    private final BankRepository bankRepository;

    /**
     * Create a new bank
     */
    public BankResponse createBank(CreateBankRequest request) {
        log.info("Creating new bank: {}", request.getBankName());

        // Check for duplicates
        if (bankRepository.existsByBankCode(request.getBankCode())) {
            throw new DuplicateResourceException("Bank", "bankCode", request.getBankCode());
        }

        if (bankRepository.existsByIfscPrefix(request.getIfscPrefix())) {
            throw new DuplicateResourceException("Bank", "ifscPrefix", request.getIfscPrefix());
        }

        Bank bank = Bank.builder()
                .bankName(request.getBankName().trim())
                .bankCode(request.getBankCode().toUpperCase())
                .ifscPrefix(request.getIfscPrefix().toUpperCase())
                .logoUrl(request.getLogoUrl())
                .upiEnabled(request.getUpiEnabled())
                .impsEnabled(request.getImpsEnabled())
                .neftEnabled(request.getNeftEnabled())
                .rtgsEnabled(request.getRtgsEnabled())
                .active(true)
                .build();

        bank = bankRepository.save(bank);
        log.info("Bank created successfully with ID: {}", bank.getId());

        return mapToBankResponse(bank);
    }

    /**
     * Get bank by ID
     */
    @Transactional(readOnly = true)
    public BankResponse getBankById(UUID bankId) {
        log.info("Fetching bank by ID: {}", bankId);

        Bank bank = bankRepository.findByIdAndActiveTrue(bankId)
                .orElseThrow(() -> new BankNotFoundException("id", bankId.toString()));

        return mapToBankResponse(bank);
    }

    /**
     * Get bank by bank code
     */
    @Transactional(readOnly = true)
    public BankResponse getBankByCode(String bankCode) {
        log.info("Fetching bank by code: {}", bankCode);

        Bank bank = bankRepository.findByBankCodeAndActiveTrue(bankCode.toUpperCase())
                .orElseThrow(() -> new BankNotFoundException("bankCode", bankCode));

        return mapToBankResponse(bank);
    }

    /**
     * Get all active banks
     */
    @Transactional(readOnly = true)
    public List<BankResponse> getAllBanks() {
        log.info("Fetching all active banks");

        return bankRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToBankResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all UPI-enabled banks
     */
    @Transactional(readOnly = true)
    public List<BankResponse> getUpiEnabledBanks() {
        log.info("Fetching all UPI-enabled banks");

        return bankRepository.findAllByUpiEnabledTrueAndActiveTrue()
                .stream()
                .map(this::mapToBankResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bank entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Bank getBankEntityById(UUID bankId) {
        return bankRepository.findByIdAndActiveTrue(bankId)
                .orElseThrow(() -> new BankNotFoundException("id", bankId.toString()));
    }

    // Helper method
    private BankResponse mapToBankResponse(Bank bank) {
        return BankResponse.builder()
                .id(bank.getId())
                .bankName(bank.getBankName())
                .bankCode(bank.getBankCode())
                .ifscPrefix(bank.getIfscPrefix())
                .logoUrl(bank.getLogoUrl())
                .upiEnabled(bank.getUpiEnabled())
                .impsEnabled(bank.getImpsEnabled())
                .neftEnabled(bank.getNeftEnabled())
                .rtgsEnabled(bank.getRtgsEnabled())
                .active(bank.getActive())
                .createdAt(bank.getCreatedAt())
                .build();
    }
}