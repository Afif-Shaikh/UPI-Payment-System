package com.project.bank_service.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {

    private final UUID accountId;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;

    public InsufficientBalanceException(UUID accountId, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Insufficient balance in account %s. Requested: %s, Available: %s",
                accountId, requestedAmount, availableBalance));
        this.accountId = accountId;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}