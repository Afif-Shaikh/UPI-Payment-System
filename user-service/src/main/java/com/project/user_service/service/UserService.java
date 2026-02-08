package com.project.user_service.service;

import com.project.user_service.dto.request.ChangePasswordRequest;
import com.project.user_service.dto.request.UserRegistrationRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.entity.User;
import com.project.user_service.exception.InvalidRequestException;
import com.project.user_service.exception.UserAlreadyExistsException;
import com.project.user_service.exception.UserNotFoundException;
import com.project.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdGeneratorService idGeneratorService;  // Add this

    /**
     * Register a new user
     */
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with phone: {}", request.getPhone());

        // Check if phone exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("phone", request.getPhone());
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Generate custom User ID
        String userId = idGeneratorService.generateUserId();

        // Build user entity
        User user = User.builder()
                .id(userId)  // Set custom ID
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .aadhaarNumber(request.getAadhaarNumber())
                .panNumber(request.getPanNumber() != null ? request.getPanNumber().toUpperCase() : null)
                .deviceId(request.getDeviceId())
                .kycVerified(false)
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        return mapToUserResponse(user);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {  // Changed from UUID to String
        log.info("Fetching user with ID: {}", userId);

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId));

        return mapToUserResponse(user);
    }

    /**
     * Get user by phone number
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByPhone(String phone) {
        log.info("Fetching user with phone: {}", phone);

        User user = userRepository.findByPhoneAndActiveTrue(phone)
                .orElseThrow(() -> new UserNotFoundException("phone", phone));

        return mapToUserResponse(user);
    }

    /**
     * Update user details
     */
    public UserResponse updateUser(String userId, UserUpdateRequest request) {  // Changed from UUID to String
        log.info("Updating user with ID: {}", userId);

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().toLowerCase().trim();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new UserAlreadyExistsException("email", newEmail);
            }
            user.setEmail(newEmail);
        }

        if (request.getAadhaarNumber() != null) {
            user.setAadhaarNumber(request.getAadhaarNumber());
        }

        if (request.getPanNumber() != null) {
            user.setPanNumber(request.getPanNumber().toUpperCase());
        }

        if (request.getDeviceId() != null) {
            user.setDeviceId(request.getDeviceId());
        }

        user = userRepository.save(user);
        log.info("User updated successfully: {}", userId);

        return mapToUserResponse(user);
    }

    /**
     * Change user password
     */
    public void changePassword(String userId, ChangePasswordRequest request) {  // Changed from UUID to String
        log.info("Changing password for user: {}", userId);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new InvalidRequestException("New password must be different from current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Verify user password
     */
    @Transactional(readOnly = true)
    public boolean verifyPassword(String phone, String password) {
        User user = userRepository.findByPhoneAndActiveTrue(phone)
                .orElseThrow(() -> new UserNotFoundException("phone", phone));

        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    /**
     * Update last login time
     */
    public void updateLastLogin(String userId) {  // Changed from UUID to String
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
        log.info("Updated last login time for user: {}", userId);
    }

    /**
     * Deactivate user (soft delete)
     */
    public void deactivateUser(String userId) {  // Changed from UUID to String
        log.info("Deactivating user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("id", userId);
        }

        userRepository.deactivateUser(userId);
        log.info("User deactivated successfully: {}", userId);
    }

    /**
     * Update KYC status
     */
    public void updateKycStatus(String userId, boolean verified) {  // Changed from UUID to String
        log.info("Updating KYC status for user {}: {}", userId, verified);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("id", userId);
        }

        userRepository.updateKycStatus(userId, verified);
        log.info("KYC status updated for user: {}", userId);
    }

    /**
     * Check if user exists by phone
     */
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    // Helper method
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .maskedAadhaar(UserResponse.maskAadhaar(user.getAadhaarNumber()))
                .panNumber(user.getPanNumber())
                .kycVerified(user.getKycVerified())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}