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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankService bankService;
    private final IdGeneratorService idGeneratorService;

    public BankAccountResponse linkAccount(LinkAccountRequest request) {
        log.info("Linking account for user: {}", request.getUserId());

        if (bankAccountRepository.existsByUserIdAndAccountNumberAndIfscCode(
                request.getUserId(), request.getAccountNumber(), request.getIfscCode())) {
            throw new DuplicateResourceException("BankAccount", "accountNumber", request.getAccountNumber());
        }

        Bank bank = bankService.getBankEntityById(request.getBankId());

        if (!request.getIfscCode().startsWith(bank.getIfscPrefix())) {
            throw new IllegalArgumentException("IFSC code does not match the selected bank");
        }

        if (request.getIsPrimary() || bankAccountRepository.countByUserIdAndActiveTrue(request.getUserId()) == 0) {
            bankAccountRepository.clearPrimaryAccount(request.getUserId());
        }

        // Generate custom Account ID
        String accountId = idGeneratorService.generateAccountId(
                request.getUserId(),
                bank.getBankCode(),
                request.getAccountType()
        );

        BankAccount account = BankAccount.builder()
                .id(accountId)
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

    @Transactional(readOnly = true)
    public BankAccountResponse getAccountById(String accountId) {
        log.info("Fetching account by ID: {}", accountId);
        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId));
        return mapToAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAccountsByUserId(String userId) {
        log.info("Fetching accounts for user: {}", userId);
        return bankAccountRepository.findAllByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BankAccountResponse getPrimaryAccount(String userId) {
        log.info("Fetching primary account for user: {}", userId);
        BankAccount account = bankAccountRepository.findByUserIdAndIsPrimaryTrueAndActiveTrue(userId)
                .orElseThrow(() -> new AccountNotFoundException("userId (primary)", userId));
        return mapToAccountResponse(account);
    }

    public void setPrimaryAccount(String userId, String accountId) {
        log.info("Setting account {} as primary for user: {}", accountId, userId);

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId));

        if (!account.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to the user");
        }

        bankAccountRepository.clearPrimaryAccount(userId);
        bankAccountRepository.setPrimaryAccount(accountId);

        log.info("Primary account updated successfully");
    }

    public void creditAccount(String accountId, BigDecimal amount) {
        log.info("Crediting {} to account: {}", amount, accountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId);
        }

        bankAccountRepository.creditBalance(accountId, amount);
        log.info("Amount credited successfully");
    }

    public void debitAccount(String accountId, BigDecimal amount) {
        log.info("Debiting {} from account: {}", amount, accountId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId));

        int updated = bankAccountRepository.debitBalance(accountId, amount);

        if (updated == 0) {
            throw new InsufficientBalanceException(accountId, amount, account.getBalance());
        }

        log.info("Amount debited successfully");
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String accountId) {
        log.info("Fetching balance for account: {}", accountId);
        BankAccount account = bankAccountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new AccountNotFoundException("id", accountId));
        return account.getBalance();
    }

    public void verifyAccount(String accountId) {
        log.info("Verifying account: {}", accountId);
        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId);
        }
        bankAccountRepository.verifyAccount(accountId);
        log.info("Account verified successfully");
    }

    public void deactivateAccount(String accountId) {
        log.info("Deactivating account: {}", accountId);
        if (!bankAccountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("id", accountId);
        }
        bankAccountRepository.deactivateAccount(accountId);
        log.info("Account deactivated successfully");
    }

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