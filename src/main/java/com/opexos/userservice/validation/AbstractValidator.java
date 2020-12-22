package com.opexos.userservice.validation;

import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

/**
 * Inheritors must implement method <code>doValidation</code>.
 * Provides useful methods for working with an incoming request.
 *
 * @param <A> constraint annotation
 * @param <T> validated class
 */
public abstract class AbstractValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {


    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        ConstraintValidatorContextWrapper contextWrapper = new ConstraintValidatorContextWrapper(context);
        doValidation(value, contextWrapper);
        return contextWrapper.isValid();
    }

    protected abstract void doValidation(T value, ConstraintValidatorContextWrapper context);

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRequestPathVariables() {
        return (Map<String, Object>) RequestContextHolder.currentRequestAttributes()
                .getAttribute(View.PATH_VARIABLES, RequestAttributes.SCOPE_REQUEST);
    }

    @SuppressWarnings("unchecked")
    public <V> V getRequestPathVariable(String pathVariable) {
        return (V) Objects.requireNonNull(getRequestPathVariables().get(pathVariable),
                String.format("Path variable '%s' is not found", pathVariable));
    }

    public HttpMethod getRequestMethod() {
        String method = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getMethod();
        return HttpMethod.valueOf(method);
    }


}
