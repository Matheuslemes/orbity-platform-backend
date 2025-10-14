package br.com.catalog.ms_orders_service_v1.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {

        log.error("[WebExceptionHandler] - [IllegalArgumentException] {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleDefault(Exception e) {

        log.error("[WebExceptionHandler] - [Exception] {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("unexpected error");

    }
}
