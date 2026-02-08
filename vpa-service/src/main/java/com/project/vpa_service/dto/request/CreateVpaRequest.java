package com.project.vpa_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVpaRequest {

    @NotBlank(message = "User ID is required")
    private String userId;  // e.g., U100001

    @NotBlank(message = "VPA handle is required")
    @Size(min = 3, max = 50, message = "VPA handle must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "VPA handle can only contain letters, numbers, dots, and underscores")
    private String vpaHandle;  // e.g., "rahul", "rahul.kumar", "9876543210"

    @NotBlank(message = "PSP ID is required")
    private String pspId;  // e.g., PSP001

    @NotBlank(message = "Linked account ID is required")
    private String linkedAccountId;  // e.g., A100001SBISAV

    @Builder.Default
    private Boolean isPrimary = false;
}