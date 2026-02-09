package com.project.bank_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "banks", indexes = {
        @Index(name = "idx_banks_ifsc_prefix", columnList = "ifsc_prefix"),
        @Index(name = "idx_banks_code", columnList = "bank_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bank {

    @Id
    @Column(name = "id", length = 20)
    private String id;  // Changed from UUID to String (e.g., BSBI001)

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "bank_code", nullable = false, unique = true, length = 10)
    private String bankCode;

    @Column(name = "ifsc_prefix", nullable = false, unique = true, length = 4)
    private String ifscPrefix;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "upi_enabled", nullable = false)
    @Builder.Default
    private Boolean upiEnabled = true;

    @Column(name = "imps_enabled", nullable = false)
    @Builder.Default
    private Boolean impsEnabled = true;

    @Column(name = "neft_enabled", nullable = false)
    @Builder.Default
    private Boolean neftEnabled = true;

    @Column(name = "rtgs_enabled", nullable = false)
    @Builder.Default
    private Boolean rtgsEnabled = true;

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
}