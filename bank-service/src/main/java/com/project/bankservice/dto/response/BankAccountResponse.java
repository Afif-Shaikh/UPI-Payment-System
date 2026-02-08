package com.project.bank_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.bank_service.entity.BankAccount.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountResponse {

    private String id;  // Changed from UUID to String (e.g., A100001SBISAV)
    private String userId;  // Changed from UUID to String (e.g., U100001)
    private String bankName;
    private String bankCode;
    private String maskedAccountNumber;
    private String ifscCode;
    private String accountHolderName;
    private AccountType accountType;
    private BigDecimal balance;
    private Boolean isPrimary;
    private Boolean isVerified;
    private Boolean active;
    private LocalDateTime createdAt;

    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        int length = accountNumber.length();
        String lastFour = accountNumber.substring(length - 4);
        return "X".repeat(length - 4) + lastFour;
    }
}