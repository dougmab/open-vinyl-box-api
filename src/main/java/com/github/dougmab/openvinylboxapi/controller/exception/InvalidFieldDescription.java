package com.github.dougmab.openvinylboxapi.controller.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class InvalidFieldDescription {
    private String fieldName;
    private String message;

    public InvalidFieldDescription() {
    }

    public InvalidFieldDescription(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public static List<InvalidFieldDescription> fromBindingResult(BindingResult bindingResult) {
        List<InvalidFieldDescription> fields = new ArrayList<>();

        for (FieldError fe : bindingResult.getFieldErrors()) {
            fields.add(new InvalidFieldDescription(fe.getField(), fe.getDefaultMessage()));
        }

        return fields;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
