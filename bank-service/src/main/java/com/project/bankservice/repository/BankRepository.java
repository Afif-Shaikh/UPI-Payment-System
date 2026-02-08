package com.project.bank_service.repository;

import com.project.bank_service.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankRepository extends JpaRepository<Bank, UUID> {

    // Find active bank by ID
    Optional<Bank> findByIdAndActiveTrue(UUID id);

    // Find by bank code
    Optional<Bank> findByBankCodeAndActiveTrue(String bankCode);

    // Find by IFSC prefix
    Optional<Bank> findByIfscPrefixAndActiveTrue(String ifscPrefix);

    // Find all active banks
    List<Bank> findAllByActiveTrue();

    // Find all UPI-enabled banks
    List<Bank> findAllByUpiEnabledTrueAndActiveTrue();

    // Check existence
    boolean existsByBankCode(String bankCode);
    boolean existsByIfscPrefix(String ifscPrefix);
}