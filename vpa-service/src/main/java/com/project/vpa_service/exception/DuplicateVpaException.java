package com.project.vpa_service.exception;

public class DuplicateVpaException extends RuntimeException {

    private final String vpaAddress;

    public DuplicateVpaException(String vpaAddress) {
        super(String.format("VPA already exists: %s", vpaAddress));
        this.vpaAddress = vpaAddress;
    }

    public String getVpaAddress() {
        return vpaAddress;
    }
}