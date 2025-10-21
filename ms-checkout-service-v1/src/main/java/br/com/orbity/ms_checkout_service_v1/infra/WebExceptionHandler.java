package br.com.orbity.ms_checkout_service_v1.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e){

        return ResponseEntity.badRequest().body(e.getMessage());

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> conflict(IllegalStateException e){

        return ResponseEntity.status(409).body(e.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> serverError(Exception e){

        log.error("unhandled error", e);

        return ResponseEntity.internalServerError().body("Internal error");

    }

}
