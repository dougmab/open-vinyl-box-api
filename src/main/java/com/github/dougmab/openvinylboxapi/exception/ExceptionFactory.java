package com.github.dougmab.openvinylboxapi.exception;

import com.github.dougmab.openvinylboxapi.entity.Product;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

public class ExceptionFactory {
    public static RuntimeException entityNotFound(Class<?> entity, Long id) {
        return new EntityNotFoundException(entity.getSimpleName() + " with ID " + id + " was not found");
    }

    public static DataIntegrityViolationException dataIntegrityViolation(Class<?> entity) {
        return new DataIntegrityViolationException(entity.getSimpleName() + " is being referenced by another entity and cannot be deleted");
    }
}
