package com.project.vpa_service.exception;

public class VpaNotFoundException extends RuntimeException {

    private final String field;
    private final String value;

    public VpaNotFoundException(String field, String value) {
        super(String.format("VPA not found with %s: %s", field, value));
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