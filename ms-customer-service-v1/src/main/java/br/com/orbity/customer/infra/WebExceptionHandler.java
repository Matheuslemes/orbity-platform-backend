package br.com.orbity.customer.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> barRequest(IllegalArgumentException e) {

        log.warn("[WebExceptionHandler] 400 {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> error(Exception e) {

        log.error("[WebExceptionHandler] 500 {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("Internal error");

    }

}
