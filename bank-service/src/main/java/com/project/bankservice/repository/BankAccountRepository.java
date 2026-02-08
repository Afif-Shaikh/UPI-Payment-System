package com.project.bank_service.repository;

import com.project.bank_service.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    // Find by ID (active only)
    Optional<BankAccount> findByIdAndActiveTrue(UUID id);

    // Find all accounts for a user
    List<BankAccount> findAllByUserIdAndActiveTrue(UUID userId);

    // Find by account number and IFSC
    Optional<BankAccount> findByAccountNumberAndIfscCodeAndActiveTrue(String accountNumber, String ifscCode);

    // Find primary account for user
    Optional<BankAccount> findByUserIdAndIsPrimaryTrueAndActiveTrue(UUID userId);

    // Check if account exists for user
    boolean existsByUserIdAndAccountNumberAndIfscCode(UUID userId, String accountNumber, String ifscCode);

    // Update balance
    @Modifying
    @Query("UPDATE BankAccount ba SET ba.balance = ba.balance + :amount, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void creditBalance(@Param("accountId") UUID accountId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.balance = ba.balance - :amount, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId AND ba.balance >= :amount")
    int debitBalance(@Param("accountId") UUID accountId, @Param("amount") BigDecimal amount);

    // Set primary account
    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isPrimary = false, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.userId = :userId AND ba.isPrimary = true")
    void clearPrimaryAccount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isPrimary = true, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void setPrimaryAccount(@Param("accountId") UUID accountId);

    // Deactivate account
    @Modifying
    @Query("UPDATE BankAccount ba SET ba.active = false, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void deactivateAccount(@Param("accountId") UUID accountId);

    // Verify account
    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isVerified = true, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void verifyAccount(@Param("accountId") UUID accountId);

    // Count accounts for user
    long countByUserIdAndActiveTrue(UUID userId);
}