package com.commander.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidateException extends RuntimeException {
    private final List<FieldViolation> fieldViolations;

    public ValidateException(List<FieldViolation> fieldViolations) {
        this.fieldViolations = fieldViolations;
    }

    public ValidateException(String message, List<FieldViolation> fieldViolations) {
        super(message);
        this.fieldViolations = fieldViolations;
    }
}
