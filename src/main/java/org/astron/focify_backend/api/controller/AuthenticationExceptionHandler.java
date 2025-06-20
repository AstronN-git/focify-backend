package org.astron.focify_backend.api.controller;

import org.astron.focify_backend.api.dto.ErrorDto;
import org.astron.focify_backend.api.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthenticationExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(exception.getMessage()));
    }
}
