package com.project.user_service.controller;

import com.project.user_service.dto.request.ChangePasswordRequest;
import com.project.user_service.dto.request.UserRegistrationRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import com.project.user_service.dto.response.ApiResponse;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received registration request for phone: {}", request.getPhone());
        UserResponse user = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable String userId) {
        log.info("Fetching user by ID: {}", userId);
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully"));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByPhone(
            @PathVariable String phone) {
        log.info("Fetching user by phone: {}", phone);
        UserResponse user = userService.getUserByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Updating user: {}", userId);
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyPassword(
            @RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String password = request.get("password");
        log.info("Verifying password for phone: {}", phone);
        boolean isValid = userService.verifyPassword(phone, password);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("valid", isValid),
                isValid ? "Password verified successfully" : "Invalid password"));
    }

    @GetMapping("/check-phone/{phone}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPhoneExists(
            @PathVariable String phone) {
        log.info("Checking if phone exists: {}", phone);
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("exists", exists),
                exists ? "Phone number is already registered" : "Phone number is available"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable String userId) {
        log.info("Deactivating user: {}", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @PutMapping("/{userId}/kyc")
    public ResponseEntity<ApiResponse<Void>> updateKycStatus(
            @PathVariable String userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean verified = request.get("verified");
        log.info("Updating KYC status for user {}: {}", userId, verified);
        userService.updateKycStatus(userId, verified);
        return ResponseEntity.ok(ApiResponse.success("KYC status updated successfully"));
    }

    @PutMapping("/{userId}/login")
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(
            @PathVariable String userId) {
        log.info("Updating last login for user: {}", userId);
        userService.updateLastLogin(userId);
        return ResponseEntity.ok(ApiResponse.success("Last login updated successfully"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "user-service"),
                "Service is healthy"));
    }
}