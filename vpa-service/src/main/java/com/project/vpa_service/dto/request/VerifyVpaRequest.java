package com.project.vpa_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyVpaRequest {

    @NotBlank(message = "VPA address is required")
    @Pattern(regexp = "^[a-zA-Z0-9._]+@[a-z0-9]+$", message = "Invalid VPA format. Use format: handle@psp")
    private String vpaAddress;  // e.g., "rahul@okaxis"
}