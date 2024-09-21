package com.github.dougmab.openvinylboxapi.controller.exception;

import com.github.dougmab.openvinylboxapi.payload.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<StandardError>> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        StandardError err = new StandardError(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(err.getStatus())
                .body(ApiResponse.error("Entity not found", err));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<StandardError>> entityNotFound(DataIntegrityViolationException e, HttpServletRequest request) {
        StandardError err = new StandardError(HttpStatus.CONFLICT.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(err.getStatus())
                .body(ApiResponse.error("Data integrity violation", err));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationError>> entityNotFound(MethodArgumentNotValidException e, HttpServletRequest request) {
        ValidationError err = new ValidationError(e, request);

        return ResponseEntity.status(err.getStatus())
                .body(ApiResponse.error("Invalid field(s)", err));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<StandardError>> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        StandardError err = new StandardError(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(err.getStatus())
                .body(ApiResponse.error("Failed to login", err));
    }
}
