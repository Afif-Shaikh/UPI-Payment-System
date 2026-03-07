package com.project.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Response")
public class UserResponse {

    @Schema(description = "Unique user ID", example = "U100001")
    private String id;

    @Schema(description = "User's full name", example = "Rahul Kumar")
    private String fullName;

    @Schema(description = "Mobile number", example = "9876543210")
    private String phone;

    @Schema(description = "Email address", example = "rahul@example.com")
    private String email;

    @Schema(description = "Masked Aadhaar number", example = "XXXX-XXXX-1234")
    private String maskedAadhaar;

    @Schema(description = "PAN card number", example = "ABCDE1234F")
    private String panNumber;

    @Schema(description = "KYC verification status", example = "false")
    private Boolean kycVerified;

    @Schema(description = "Account active status", example = "true")
    private Boolean active;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLoginAt;

    public static String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() != 12) {
            return null;
        }
        return "XXXX-XXXX-" + aadhaar.substring(8);
    }
}