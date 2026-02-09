package com.project.bank_service.dto.response;

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
public class BankResponse {

    private String id;  // Changed from UUID to String (e.g., BSBI001)
    private String bankName;
    private String bankCode;
    private String ifscPrefix;
    private String logoUrl;
    private Boolean upiEnabled;
    private Boolean impsEnabled;
    private Boolean neftEnabled;
    private Boolean rtgsEnabled;
    private Boolean active;
    private LocalDateTime createdAt;
}