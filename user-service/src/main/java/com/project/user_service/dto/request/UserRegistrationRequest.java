package com.project.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User Registration Request")
public class UserRegistrationRequest {

    @Schema(description = "User's full name", example = "Rahul Kumar", required = true)
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Schema(description = "Indian mobile number (10 digits starting with 6-9)", example = "9876543210", required = true)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number (must be 10 digits starting with 6-9)")
    private String phone;

    @Schema(description = "Email address", example = "rahul@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password (min 8 chars with uppercase, lowercase, number, and special char)",
            example = "Password@123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character (@#$%^&+=!)"
    )
    private String password;

    @Schema(description = "12-digit Aadhaar number", example = "234567891234")
    @Pattern(regexp = "^[2-9]\\d{11}$", message = "Invalid Aadhaar number")
    private String aadhaarNumber;

    @Schema(description = "PAN card number", example = "ABCDE1234F")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN number format")
    private String panNumber;

    @Schema(description = "Device identifier for security", example = "device-uuid-12345")
    private String deviceId;
}