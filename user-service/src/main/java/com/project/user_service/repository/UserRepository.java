package com.project.user_service.repository;

import com.project.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {  // Changed from UUID to String

    Optional<User> findByPhoneAndActiveTrue(String phone);

    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByIdAndActiveTrue(String id);  // Changed from UUID to String

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") String userId, @Param("loginTime") LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE User u SET u.active = false, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void deactivateUser(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE User u SET u.kycVerified = :verified, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateKycStatus(@Param("userId") String userId, @Param("verified") boolean verified);
}