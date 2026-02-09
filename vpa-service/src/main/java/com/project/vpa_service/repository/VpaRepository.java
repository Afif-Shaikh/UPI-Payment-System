package com.project.vpa_service.repository;

import com.project.vpa_service.entity.Vpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VpaRepository extends JpaRepository<Vpa, String> {

    // Find by ID (active only)
    Optional<Vpa> findByIdAndActiveTrue(String id);

    // Find by VPA address (e.g., rahul@okaxis)
    Optional<Vpa> findByVpaAddressAndActiveTrue(String vpaAddress);

    // Find all VPAs for a user
    List<Vpa> findAllByUserIdAndActiveTrue(String userId);

    // Find primary VPA for user
    Optional<Vpa> findByUserIdAndIsPrimaryTrueAndActiveTrue(String userId);

    // Check if VPA address exists
    boolean existsByVpaAddress(String vpaAddress);

    // Check if user has VPA with same handle and PSP
    boolean existsByUserIdAndVpaHandleAndPspId(String userId, String vpaHandle, String pspId);

    // Count VPAs for user
    long countByUserIdAndActiveTrue(String userId);

    // Clear primary VPA for user
    @Modifying
    @Query("UPDATE Vpa v SET v.isPrimary = false, v.updatedAt = CURRENT_TIMESTAMP WHERE v.userId = :userId AND v.isPrimary = true")
    void clearPrimaryVpa(@Param("userId") String userId);

    // Set VPA as primary
    @Modifying
    @Query("UPDATE Vpa v SET v.isPrimary = true, v.updatedAt = CURRENT_TIMESTAMP WHERE v.id = :vpaId")
    void setPrimaryVpa(@Param("vpaId") String vpaId);

    // Verify VPA
    @Modifying
    @Query("UPDATE Vpa v SET v.isVerified = true, v.updatedAt = CURRENT_TIMESTAMP WHERE v.id = :vpaId")
    void verifyVpa(@Param("vpaId") String vpaId);

    // Deactivate VPA
    @Modifying
    @Query("UPDATE Vpa v SET v.active = false, v.updatedAt = CURRENT_TIMESTAMP WHERE v.id = :vpaId")
    void deactivateVpa(@Param("vpaId") String vpaId);

    // Update linked account
    @Modifying
    @Query("UPDATE Vpa v SET v.linkedAccountId = :accountId, v.updatedAt = CURRENT_TIMESTAMP WHERE v.id = :vpaId")
    void updateLinkedAccount(@Param("vpaId") String vpaId, @Param("accountId") String accountId);
}