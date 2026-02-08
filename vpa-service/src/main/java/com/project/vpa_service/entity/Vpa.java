package com.project.vpa_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vpas", indexes = {
        @Index(name = "idx_vpas_user_id", columnList = "user_id"),
        @Index(name = "idx_vpas_vpa_address", columnList = "vpa_address", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vpa {

    @Id
    @Column(name = "id", length = 20)
    private String id;  // e.g., VPA100001

    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;  // e.g., U100001

    @Column(name = "vpa_handle", nullable = false, length = 50)
    private String vpaHandle;  // e.g., "rahul", "9876543210"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psp_id", nullable = false)
    private Psp psp;

    @Column(name = "vpa_address", nullable = false, unique = true, length = 100)
    private String vpaAddress;  // e.g., "rahul@okaxis"

    @Column(name = "linked_account_id", nullable = false, length = 30)
    private String linkedAccountId;  // e.g., A100001SBISAV

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
        // Generate VPA address
        if (vpaAddress == null && vpaHandle != null && psp != null) {
            vpaAddress = vpaHandle.toLowerCase() + "@" + psp.getPspHandle().toLowerCase();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}