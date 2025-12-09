package com.studium.studium_academico.infrastructure.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalHandlerException extends RuntimeException {
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleExpired(TokenExpiredException ex) {
        return ResponseEntity.status(410).body(Map.of(
                "error", "token_expired",
                "message", ex.getMessage()
        ));
    }
}
