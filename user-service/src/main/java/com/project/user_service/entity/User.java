package com.project.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_phone", columnList = "phone"),
        @Index(name = "idx_users_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "id", length = 20)
    private String id;  // Changed from UUID to String (e.g., U100001)

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "aadhaar_number", length = 12)
    private String aadhaarNumber;

    @Column(name = "pan_number", length = 10)
    private String panNumber;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "kyc_verified", nullable = false)
    @Builder.Default
    private Boolean kycVerified = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

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