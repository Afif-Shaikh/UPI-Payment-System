package com.project.bank_service.service;

import com.project.bank_service.dto.request.LinkAccountRequest;
import com.project.bank_service.dto.response.BankAccountResponse;
import com.project.bank_service.entity.Bank;
import com.project.bank_service.entity.BankAccount;
import com.project.bank_service.exception.AccountNotFoundException;
import com.project.bank_service.exception.DuplicateResourceException;
import com.project.bank_service.exception.InsufficientBalanceException;
import com.project.bank_service.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankService bankService;

    /**
     * Link a new bank account
     */
    public BankAccountResponse linkAccount(LinkAccountRequest request) {
        log.info("Linking account for user: {}", request.getUserId());

        // Check if account already linked
        if (bankAccountRepository.existsByUserIdAndAccountNumberAndIfscCode(
                request.getUserId(), request.getAccountNumber(), request.getIfscCode())) {
            throw new DuplicateResourceException("BankAccount", "accountNumber", request.getAccountNumber());
        }

        // Get bank
        Bank bank = bankService.getBankEntityById(request.getBankId());

        // Validate IFSC prefix matches bank
        if (!request.getIfscCode().startsWith(bank.getIfscPrefix())) {
            throw new IllegalArgumentException("IFSC code does not match the selected bank");
        }

        // If this is the first account or marked as primary, clear existing primary
        if (request.getIsPrimary() || bankAccountRepository.countByUserIdAndActiveTrue(request.getUserId()) == 0) {
            bankAccountRepository.clearPrimaryAccount(request.getUserId());
        }

        BankAccount account = BankAccount.builder()
                .userId(request.getUserId())
                .bank(bank)
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode().toUpperCase())
                .accountHolderName(request.getAccountHolderName().trim())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .isPrimary(request.getIsPrimary() || bankAccountRepository.countByUserIdAndActiveTrue(request.getUserId()) == 0)
                .isVerified(false)
                .active(true)
                .build();

        account = bankAccountRepository.save(account);
        log.info("Account linked successfully with ID: {}", account.getId());

        return mapToAccountResponse(account);
    }

    /**
     * Get account by ID
     */
    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(UUID accountId) {
        log.info("Fetching account by ID: {}", accountId);

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId.toString()));

        return mapToAccountResponse(account);
    }

    /**
     * Get all accounts for a user
     */
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAccountsByUserId(UUID userId) {
        log.info("Fetching accounts for user: {}", userId);

        return bankAccountRepository.findAllByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get primary account for a user
     */
    @Transactional(readOnly = true)
    public BankAccountResponse getPrimaryAccount(UUID userId) {
        log.info("Fetching primary account for user: {}", userId);

        BankAccount account = bankAccountRepository.findByUserIdAndIsPrimaryTrueAndActiveTrue(userId)
                .orElseThrow(() -> new AccountNotFoundException("userId (primary)", userId.toString()));

        return mapToAccountResponse(account);
    }

    /**
     * Set account as primary
     */
    public void setPrimaryAccount(UUID userId, UUID accountId) {
        log.info("Setting account {} as primary for user: {}", accountId, userId);

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId.toString()));

        if (!account.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to the user");
        }

        bankAccountRepository.clearPrimaryAccount(userId);
        bankAccountRepository.setPrimaryAccount(accountId);

        log.info("Primary account updated successfully");
    }

    /**
     * Credit amount to account
     */
    public void creditAccount(UUID accountId, BigDecimal amount) {
        log.info("Crediting {} to account: {}", amount, accountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId.toString());
        }

        bankAccountRepository.creditBalance(accountId, amount);
        log.info("Amount credited successfully");
    }

    /**
     * Debit amount from account
     */
    public void debitAccount(UUID accountId, BigDecimal amount) {
        log.info("Debiting {} from account: {}", amount, accountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId.toString()));

        int updated = bankAccountRepository.debitBalance(accountId, amount);

        if (updated == 0) {
            throw new InsufficientBalanceException(accountId, amount, account.getBalance());
        }

        log.info("Amount debited successfully");
    }

    /**
     * Get account balance
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId) {
        log.info("Fetching balance for account: {}", accountId);

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId.toString()));

        return account.getBalance();
    }

    /**
     * Verify account
     */
    public void verifyAccount(UUID accountId) {
        log.info("Verifying account: {}", accountId);

        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId.toString());
        }

        bankAccountRepository.verifyAccount(accountId);
        log.info("Account verified successfully");
    }

    /**
     * Deactivate account
     */
    public void deactivateAccount(UUID accountId) {
        log.info("Deactivating account: {}", accountId);

        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId.toString());
        }

        bankAccountRepository.deactivateAccount(accountId);
        log.info("Account deactivated successfully");
    }

    // Helper method
    private BankAccountResponse mapToAccountResponse(BankAccount account) {
        return BankAccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .bankName(account.getBank().getBankName())
                .bankCode(account.getBank().getBankCode())
                .maskedAccountNumber(BankAccountResponse.maskAccountNumber(account.getAccountNumber()))
                .ifscCode(account.getIfscCode())
                .accountHolderName(account.getAccountHolderName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .isPrimary(account.getIsPrimary())
                .isVerified(account.getIsVerified())
                .active(account.getActive())
                .createdAt(account.getCreatedAt())
                .build();
    }
}