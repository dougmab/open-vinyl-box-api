package com.github.dougmab.openvinylboxapi.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public class ValidationError extends StandardError {

    private List<InvalidFieldDescription> errors;

    public ValidationError(MethodArgumentNotValidException e, HttpServletRequest request) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error", request.getRequestURI());
        this.errors = InvalidFieldDescription.fromBindingResult(e.getBindingResult());
    }

    public List<InvalidFieldDescription> getErrors() {
        return errors;
    }

    public void setErrors(List<InvalidFieldDescription> errors) {
        this.errors = errors;
    }
}
