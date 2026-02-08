package com.project.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class UserResponse {

    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String maskedAadhaar;
    private String panNumber;
    private Boolean kycVerified;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() != 12) {
            return null;
        }
        return "XXXX-XXXX-" + aadhaar.substring(8);
    }
}