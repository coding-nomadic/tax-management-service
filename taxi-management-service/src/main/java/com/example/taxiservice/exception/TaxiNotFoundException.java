package com.example.taxiservice.exception;

public class TaxiNotFoundException extends RuntimeException {
    private String message;
    private String errorCode;

    public TaxiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaxiNotFoundException(String message, String errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
