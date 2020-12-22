package com.opexos.userservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSourceAccessor message;

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleBadRequestException(BadRequestException e) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ExceptionDTO> handleMethodArgumentNotValidException(BindException e) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(message.getMessage("validation.invalid-values"))
                        .fields(getWrongFields(e.getBindingResult()))
                        .build());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ExceptionDTO> handleDataTypesExceptions(Exception e) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(message.getMessage("error.incorrect-request"))
                        .build());
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingServletRequestPartException.class,
    })
    public ResponseEntity<ExceptionDTO> handleHttpExceptions(Exception e) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(message.getMessage("error.upload-size"))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionDTO> handleOtherExceptions(HttpServletRequest request, Exception e)
            throws Exception {
        if (e instanceof org.apache.catalina.connector.ClientAbortException) {
            //spring handles this exception correctly
            throw e;
        }

        log.error("An unhandled exception occurred during {}", getEndpoint(request), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionDTO.builder()
                        .message(message.getMessage("error.internal-server-error"))
                        .build());
    }

    private String getEndpoint(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder()
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURI());
        String query = request.getQueryString();
        if (query == null) {
            return sb.toString();
        } else {
            return sb.append('?').append(query).toString();
        }
    }


    private List<FieldErrorDTO> getWrongFields(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(it -> FieldErrorDTO.builder()
                        .field(it.getField())
                        .message(it.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
    }
}


