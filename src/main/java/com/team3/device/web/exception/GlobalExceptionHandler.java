package com.team3.device.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "NOT_FOUND",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(BusinessValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "BUSINESS_VALIDATION_ERROR",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DUPLICATE_EMAIL",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DUPLICATE_RESOURCE",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }
}
