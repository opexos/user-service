package com.opexos.userservice.service.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserValidatorImpl.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserValidator {
    String message() default "Incorrect value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
