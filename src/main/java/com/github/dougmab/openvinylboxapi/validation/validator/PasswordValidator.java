package com.github.dougmab.openvinylboxapi.validation.validator;

import com.github.dougmab.openvinylboxapi.validation.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private boolean hasUppercase;
    private boolean hasLowercase;
    private boolean hasSpecialChar;
    private int min;
    private int max;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    @Override
    public void initialize(Password constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        this.hasUppercase = constraintAnnotation.hasUppercase();
        this.hasLowercase = constraintAnnotation.hasLowercase();
        this.hasSpecialChar = constraintAnnotation.hasSpecialChar();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null || password.length() < min || password.length() > max) {
            return false;
        }

        if (hasUppercase && !UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        if (hasLowercase && !LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        if (hasSpecialChar && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }

        return true;
    }
}
