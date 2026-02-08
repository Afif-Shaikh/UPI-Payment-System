package com.project.bank_service.exception;

public class DuplicateResourceException extends RuntimeException {

    private final String resource;
    private final String field;
    private final String value;

    public DuplicateResourceException(String resource, String field, String value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}