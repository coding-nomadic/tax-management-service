package com.example.taxiservice.exception;

public class TaxiNotFoundException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TaxiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaxiNotFoundException(String message, String errorCode) {
        super(message);
    }
}
