package com.project.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number. Must be 10 digits starting with 6-9")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$",
            message = "Password must contain at least one letter and one number")
    private String password;

    @Size(min = 12, max = 12, message = "Aadhaar number must be exactly 12 digits")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must contain only digits")
    private String aadhaarNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    private String deviceId;
}