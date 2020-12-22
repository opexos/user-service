package com.opexos.userservice.validation;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorContext;

/**
 * Wrapper that tracks the call to the <code>buildConstraintViolationWithTemplate</code>,
 * and provides an <code>isValid()</code> method that returns false if one was called
 */
public class ConstraintValidatorContextWrapper {
    private final ConstraintValidatorContext context;
    private boolean isValid = true;

    public ConstraintValidatorContextWrapper(ConstraintValidatorContext context) {
        this.context = context;
    }

    public void disableDefaultConstraintViolation() {
        context.disableDefaultConstraintViolation();
    }

    public String getDefaultConstraintMessageTemplate() {
        return context.getDefaultConstraintMessageTemplate();
    }

    public ClockProvider getClockProvider() {
        return context.getClockProvider();
    }

    public ConstraintValidatorContext.ConstraintViolationBuilder buildConstraintViolationWithTemplate(
            String messageTemplate) {
        this.isValid = false;
        return context.buildConstraintViolationWithTemplate(messageTemplate);
    }

    public <T> T unwrap(Class<T> type) {
        return context.unwrap(type);
    }

    public void addConstraintViolation(String property, String message) {
        buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }

    /**
     * Returns false if <code>buildConstraintViolationWithTemplate</code> was called
     */
    public boolean isValid() {
        return isValid;
    }
}