package com.github.dougmab.openvinylboxapi.exception;

import jakarta.persistence.EntityNotFoundException;

public class ExceptionFactory {
    public static RuntimeException entityNotFound(Class<?> entity, Long id) {
        return new EntityNotFoundException(entity.getSimpleName() + " with ID " + id + " was not found");
    }
}
