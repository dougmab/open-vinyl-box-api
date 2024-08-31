package com.github.dougmab.openvinylboxapi.controller.exception;

import com.github.dougmab.openvinylboxapi.payload.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<StandardError>> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        StandardError err = new StandardError(e, request);
        err.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Entity not found", err));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<StandardError>> entityNotFound(DataIntegrityViolationException e, HttpServletRequest request) {
        StandardError err = new StandardError(e, request);
        err.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Data integrity violation", err));
    }
}
