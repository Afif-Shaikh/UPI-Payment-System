package com.project.vpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "psps", indexes = {
        @Index(name = "idx_psps_handle", columnList = "psp_handle")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Psp {

    @Id
    @Column(name = "id", length = 20)
    private String id;  // e.g., PSP001

    @Column(name = "psp_name", nullable = false, length = 100)
    private String pspName;  // e.g., "Google Pay", "PhonePe"

    @Column(name = "psp_handle", nullable = false, unique = true, length = 20)
    private String pspHandle;  // e.g., "okaxis", "ybl", "paytm"

    @Column(name = "bank_name", length = 100)
    private String bankName;  // Associated bank, e.g., "Axis Bank"

    @Column(name = "bank_ifsc_prefix", length = 4)
    private String bankIfscPrefix;  // e.g., "UTIB" for Axis

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

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