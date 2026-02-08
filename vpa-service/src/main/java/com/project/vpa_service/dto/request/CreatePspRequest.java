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
public class CreatePspRequest {

    @NotBlank(message = "PSP name is required")
    @Size(min = 2, max = 100, message = "PSP name must be between 2 and 100 characters")
    private String pspName;  // e.g., "Google Pay"

    @NotBlank(message = "PSP handle is required")
    @Size(min = 2, max = 20, message = "PSP handle must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-z0-9]+$", message = "PSP handle must be lowercase alphanumeric")
    private String pspHandle;  // e.g., "okaxis"

    private String bankName;  // e.g., "Axis Bank"

    @Pattern(regexp = "^[A-Z]{4}$", message = "Bank IFSC prefix must be 4 uppercase letters")
    private String bankIfscPrefix;  // e.g., "UTIB"

    private String logoUrl;
}