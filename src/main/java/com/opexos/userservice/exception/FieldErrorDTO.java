package com.opexos.userservice.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FieldErrorDTO {
    private final String field;
    private final String message;
}
