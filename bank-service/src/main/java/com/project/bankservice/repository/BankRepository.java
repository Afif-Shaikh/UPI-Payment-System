package com.project.bank_service.repository;

import com.project.bank_service.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {  // Changed from UUID to String

    Optional<Bank> findByIdAndActiveTrue(String id);

    Optional<Bank> findByBankCodeAndActiveTrue(String bankCode);

    Optional<Bank> findByIfscPrefixAndActiveTrue(String ifscPrefix);

    List<Bank> findAllByActiveTrue();

    List<Bank> findAllByUpiEnabledTrueAndActiveTrue();

    boolean existsByBankCode(String bankCode);

    boolean existsByIfscPrefix(String ifscPrefix);
}