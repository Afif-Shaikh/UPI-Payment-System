package com.project.bank_service.dto.request;

import com.project.bank_service.entity.BankAccount.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class LinkAccountRequest {

    @NotBlank(message = "User ID is required")
    private String userId;  // Changed from UUID to String (e.g., U100001)

    @NotBlank(message = "Bank ID is required")
    private String bankId;  // Changed from UUID to String (e.g., BSBI001)

    @NotBlank(message = "Account number is required")
    @Size(min = 9, max = 18, message = "Account number must be between 9 and 18 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Account number must contain only digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;

    @NotBlank(message = "Account holder name is required")
    @Size(min = 2, max = 100, message = "Account holder name must be between 2 and 100 characters")
    private String accountHolderName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @Builder.Default
    private Boolean isPrimary = false;
}