package com.project.bank_service.service;

import com.project.bank_service.entity.BankAccount.AccountType;
import com.project.bank_service.entity.IdSequence;
import com.project.bank_service.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdGeneratorService {

    private final IdSequenceRepository idSequenceRepository;

    /**
     * Generate Bank ID like BSBI001, BHDFC002
     * Format: B + bankCode + 3-digit sequence
     */
    @Transactional
    public String generateBankId(String bankCode) {
        String sequenceName = "BANK_" + bankCode.toUpperCase();
        long nextValue = getNextSequenceValue(sequenceName, 1L);
        String bankId = "B" + bankCode.toUpperCase() + String.format("%03d", nextValue);
        log.debug("Generated Bank ID: {}", bankId);
        return bankId;
    }

    /**
     * Generate Account ID like A100001SBISAV, A100001HDFCCUR
     * Format: A + userSequence + bankCode + accountTypeShort
     */
    @Transactional
    public String generateAccountId(String userId, String bankCode, AccountType accountType) {
        // Extract user sequence from userId (e.g., U100001 -> 100001)
        String userSeq = userId.startsWith("U") ? userId.substring(1) : userId;
        String typeShort = getAccountTypeShort(accountType);

        String accountId = "A" + userSeq + bankCode.toUpperCase() + typeShort;
        log.debug("Generated Account ID: {}", accountId);
        return accountId;
    }

    /**
     * Get short code for account type
     */
    private String getAccountTypeShort(AccountType accountType) {
        return switch (accountType) {
            case SAVINGS -> "SAV";
            case CURRENT -> "CUR";
            case SALARY -> "SAL";
            case NRI -> "NRI";
            case FIXED_DEPOSIT -> "FD";
//            case LOAN -> "LOAN";
        };
    }

    /**
     * Get next sequence value (thread-safe)
     */
    private long getNextSequenceValue(String sequenceName, long startValue) {
        IdSequence sequence = idSequenceRepository.findBySequenceNameForUpdate(sequenceName)
                .orElseGet(() -> {
                    IdSequence newSeq = new IdSequence();
                    newSeq.setSequenceName(sequenceName);
                    newSeq.setCurrentValue(startValue);
                    return idSequenceRepository.save(newSeq);
                });

        long currentValue = sequence.getCurrentValue();
        sequence.setCurrentValue(currentValue + 1);
        idSequenceRepository.save(sequence);

        return currentValue;
    }
}