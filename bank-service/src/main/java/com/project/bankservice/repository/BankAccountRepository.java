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

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {  // Changed from UUID to String

    Optional<BankAccount> findByIdAndActiveTrue(String id);

    List<BankAccount> findAllByUserIdAndActiveTrue(String userId);

    Optional<BankAccount> findByAccountNumberAndIfscCodeAndActiveTrue(String accountNumber, String ifscCode);

    Optional<BankAccount> findByUserIdAndIsPrimaryTrueAndActiveTrue(String userId);

    boolean existsByUserIdAndAccountNumberAndIfscCode(String userId, String accountNumber, String ifscCode);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.balance = ba.balance + :amount, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void creditBalance(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.balance = ba.balance - :amount, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId AND ba.balance >= :amount")
    int debitBalance(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isPrimary = false, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.userId = :userId AND ba.isPrimary = true")
    void clearPrimaryAccount(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isPrimary = true, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void setPrimaryAccount(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.active = false, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void deactivateAccount(@Param("accountId") String accountId);

    @Modifying
    @Query("UPDATE BankAccount ba SET ba.isVerified = true, ba.updatedAt = CURRENT_TIMESTAMP WHERE ba.id = :accountId")
    void verifyAccount(@Param("accountId") String accountId);

    long countByUserIdAndActiveTrue(String userId);
}