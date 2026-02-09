package com.project.vpa_service.exception;

public class PspNotFoundException extends RuntimeException {

    private final String field;
    private final String value;

    public PspNotFoundException(String field, String value) {
        super(String.format("PSP not found with %s: %s", field, value));
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