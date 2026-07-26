package com.chakra.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class TodayOrFutureValidator implements ConstraintValidator<TodayOrFuture, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull se encarga de validar null
        }
        return !value.isBefore(LocalDate.now());
    }
}
