package com.project.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$",
            message = "Password must contain at least one letter and one number")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}