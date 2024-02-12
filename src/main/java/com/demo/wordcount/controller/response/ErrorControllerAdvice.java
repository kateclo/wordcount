package com.demo.wordcount.controller.response;

import com.demo.wordcount.exception.IllegalArgumentException;

import com.demo.wordcount.exception.IllegalOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice(basePackages = {"com.demo.wordcount.controller"})
public class ErrorControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, ServletWebRequest request) {
        log.error(String.format("[Bad request] Failed to process %s request to %s",
                request.getHttpMethod(),
                getRequestUri(request)), ex);

        Map<String, String> errors = getErrors(ex);
        String customErrorMessage = "Validation error: " + errors;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.error(SC_BAD_REQUEST, customErrorMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, ServletWebRequest request) {
        log.error(String.format("[Bad Request] Failed to process %s request to %s",
                request.getHttpMethod(),
                getRequestUri(request)), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.error(SC_BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(IllegalOperationException.class)
    protected ResponseEntity<Object> handleIllegalOperationException(IllegalOperationException ex, ServletWebRequest request) {
        log.error(String.format("[Internal Error] Encountered exception in processing %s request to %s",
                request.getHttpMethod(),
                getRequestUri(request)), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(SC_INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(NullPointerException ex, ServletWebRequest request) {
        String customErrorMessage = String.format("Encountered NPE in processing %s request to %s",
                request.getHttpMethod(),
                getRequestUri(request));
        log.error("[Internal Error] " + customErrorMessage, ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(SC_INTERNAL_SERVER_ERROR, customErrorMessage));
    }

    private Map<String, String> getErrors(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

    private String getRequestUri(ServletWebRequest request) {
        return request.getRequest().getRequestURI();
    }
}
