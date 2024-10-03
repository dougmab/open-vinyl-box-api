package com.github.dougmab.openvinylboxapi.validation.annotation;

import com.github.dougmab.openvinylboxapi.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {

    String message() default "Invalid password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean hasUppercase() default true;

    boolean hasLowercase() default true;

    boolean hasSpecialChar() default true;

    int min() default 8;

    int max() default 80;
}
