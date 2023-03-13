package com.example.taxiservice.exception;

import com.example.taxiservice.model.ErrorDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(TaxiNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleTaxiIdNotFoundException(TaxiNotFoundException e) {
        return new ResponseEntity<>(new ErrorDTO(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                                        HttpStatus.BAD_REQUEST);
    }
}
