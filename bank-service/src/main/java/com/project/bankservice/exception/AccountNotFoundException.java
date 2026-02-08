package com.project.bank_service.exception;

public class AccountNotFoundException extends RuntimeException {

    private final String field;
    private final String value;

    public AccountNotFoundException(String field, String value) {
        super(String.format("Bank account not found with %s: %s", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}