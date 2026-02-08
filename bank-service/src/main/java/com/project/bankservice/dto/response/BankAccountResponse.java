package com.project.bank_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.bank_service.entity.BankAccount.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountResponse {

    private UUID id;
    private UUID userId;
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

    /**
     * Mask account number - show only last 4 digits
     * Example: XXXXXX1234
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        int length = accountNumber.length();
        String lastFour = accountNumber.substring(length - 4);
        return "X".repeat(length - 4) + lastFour;
    }
}