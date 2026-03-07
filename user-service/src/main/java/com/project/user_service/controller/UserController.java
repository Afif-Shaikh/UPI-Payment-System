package com.project.user_service.controller;

import com.project.user_service.dto.request.ChangePasswordRequest;
import com.project.user_service.dto.request.UserRegistrationRequest;
import com.project.user_service.dto.request.UserUpdateRequest;
import com.project.user_service.dto.response.ApiResponse;
import com.project.user_service.dto.response.UserResponse;
import com.project.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details. Phone and email must be unique."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "User already exists with phone or email",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received registration request for phone: {}", request.getPhone());
        UserResponse user = userService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user details by their unique ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Fetching user by ID: {}", userId);
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully"));
    }

    @Operation(
            summary = "Get user by phone number",
            description = "Retrieves user details by their registered phone number"
    )
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByPhone(
            @Parameter(description = "Phone number", example = "9876543210")
            @PathVariable String phone) {
        log.info("Fetching user by phone: {}", phone);
        UserResponse user = userService.getUserByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(user, "User fetched successfully"));
    }

    @Operation(
            summary = "Update user details",
            description = "Updates user profile information"
    )
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Updating user: {}", userId);
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the user's password after verifying current password"
    )
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @Operation(
            summary = "Verify password",
            description = "Verifies if the provided password is correct for the given phone number"
    )
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

    @Operation(
            summary = "Check phone availability",
            description = "Checks if a phone number is already registered"
    )
    @GetMapping("/check-phone/{phone}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPhoneExists(
            @Parameter(description = "Phone number to check", example = "9876543210")
            @PathVariable String phone) {
        log.info("Checking if phone exists: {}", phone);
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("exists", exists),
                exists ? "Phone number is already registered" : "Phone number is available"));
    }

    @Operation(
            summary = "Deactivate user",
            description = "Soft deletes a user account (marks as inactive)"
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Deactivating user: {}", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @Operation(
            summary = "Update KYC status",
            description = "Updates the KYC verification status of a user (internal API)"
    )
    @PutMapping("/{userId}/kyc")
    public ResponseEntity<ApiResponse<Void>> updateKycStatus(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId,
            @RequestBody Map<String, Boolean> request) {
        Boolean verified = request.get("verified");
        log.info("Updating KYC status for user {}: {}", userId, verified);
        userService.updateKycStatus(userId, verified);
        return ResponseEntity.ok(ApiResponse.success("KYC status updated successfully"));
    }

    @Operation(
            summary = "Update last login",
            description = "Updates the last login timestamp for a user (internal API)"
    )
    @PutMapping("/{userId}/login")
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(
            @Parameter(description = "User ID", example = "U100001")
            @PathVariable String userId) {
        log.info("Updating last login for user: {}", userId);
        userService.updateLastLogin(userId);
        return ResponseEntity.ok(ApiResponse.success("Last login updated successfully"));
    }

    @Operation(
            summary = "Health check",
            description = "Returns the health status of the User Service"
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "user-service"),
                "Service is healthy"));
    }
}

