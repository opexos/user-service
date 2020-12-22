package com.opexos.userservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ExceptionDTO {
    private final String message;
    private final List<FieldErrorDTO> fields;
}
