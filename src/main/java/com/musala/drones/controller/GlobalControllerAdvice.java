package com.musala.drones.controller;

import com.musala.drones.model.OutputResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public OutputResult handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder builder = new StringBuilder("Request is not valid: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            builder.append(fieldName).append(": ").append(errorMessage).append("; ");
        });
        return OutputResult.error(builder.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public OutputResult handleMessageNotReadableExceptions(HttpMessageNotReadableException e) {
        log.warn("Handle HttpMessageNotReadableException: ", e);
        return OutputResult.error("Can't parse request: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public OutputResult handleDuplicateKeyExceptions(DuplicateKeyException e) {
        log.warn("Handle DuplicateKeyException: ", e);
        return OutputResult.error("Entity with such key already exists : " + e.getMessage());
    }
}
