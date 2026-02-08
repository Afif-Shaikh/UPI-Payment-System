package com.project.bank_service.dto.request;

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
public class CreateBankRequest {

    @NotBlank(message = "Bank name is required")
    @Size(min = 2, max = 100, message = "Bank name must be between 2 and 100 characters")
    private String bankName;

    @NotBlank(message = "Bank code is required")
    @Size(min = 3, max = 10, message = "Bank code must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Bank code must contain only uppercase letters and numbers")
    private String bankCode;

    @NotBlank(message = "IFSC prefix is required")
    @Pattern(regexp = "^[A-Z]{4}$", message = "IFSC prefix must be exactly 4 uppercase letters")
    private String ifscPrefix;

    private String logoUrl;

    @Builder.Default
    private Boolean upiEnabled = true;

    @Builder.Default
    private Boolean impsEnabled = true;

    @Builder.Default
    private Boolean neftEnabled = true;

    @Builder.Default
    private Boolean rtgsEnabled = true;
}