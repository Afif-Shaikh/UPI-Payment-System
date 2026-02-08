package com.project.bank_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts", indexes = {
        @Index(name = "idx_accounts_user_id", columnList = "user_id"),
        @Index(name = "idx_accounts_account_number", columnList = "account_number"),
        @Index(name = "idx_accounts_ifsc", columnList = "ifsc_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @Column(name = "id", length = 30)
    private String id;  // Changed from UUID to String (e.g., A100001SBISAV)

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;  // Changed from UUID to String (e.g., U100001)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "ifsc_code", nullable = false, length = 11)
    private String ifscCode;

    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AccountType {
        SAVINGS,
        CURRENT,
        SALARY,
        NRI,
        FIXED_DEPOSIT
    }
}2