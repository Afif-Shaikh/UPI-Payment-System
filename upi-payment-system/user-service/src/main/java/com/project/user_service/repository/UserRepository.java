package com.project.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.user_service.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Find a user by phone number
    Optional<User> findByPhone(String phone);

    // Find a user by email
    Optional<User> findByEmail(String email);
}
