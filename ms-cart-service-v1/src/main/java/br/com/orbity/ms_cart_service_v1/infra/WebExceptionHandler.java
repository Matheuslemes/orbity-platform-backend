package br.com.orbity.ms_cart_service_v1.infra;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class WebExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        var errors = new ArrayList<FieldViolation>();
        for (var err : ex.getBindingResult().getFieldErrors()) {
            errors.add(new FieldViolation(err.getField(), safe(err.getDefaultMessage()), safe(err.getRejectedValue())));
        }
        var body = base(req, HttpStatus.UNPROCESSABLE_ENTITY, "validation_failed")
                .withMessage("Um ou mais campos são inválidos.")
                .withFieldErrors(errors)
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex,
                                                          HttpServletRequest req) {
        var errors = new ArrayList<FieldViolation>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            errors.add(new FieldViolation(String.valueOf(v.getPropertyPath()),
                    safe(v.getMessage()), safe(v.getInvalidValue())));
        }
        var body = base(req, HttpStatus.UNPROCESSABLE_ENTITY, "constraint_violation")
                .withMessage("Validação de parâmetros falhou.")
                .withFieldErrors(errors)
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadPayload(HttpMessageNotReadableException ex,
                                                          HttpServletRequest req) {
        var body = base(req, HttpStatus.BAD_REQUEST, "malformed_json")
                .withMessage("Payload JSON inválido ou malformado.")
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest req) {
        var body = base(req, HttpStatus.BAD_REQUEST, "bad_request")
                .withMessage(safe(ex.getMessage()))
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex,
                                                            HttpServletRequest req) {
        var body = base(req, HttpStatus.CONFLICT, "conflict")
                .withMessage(safe(ex.getMessage()))
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex,
                                                        HttpServletRequest req) {
        var body = base(req, HttpStatus.NOT_FOUND, "not_found")
                .withMessage(safe(ex.getMessage()))
                .build();
        logWarn(body, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        var body = base(req, HttpStatus.INTERNAL_SERVER_ERROR, "internal_error")
                .withMessage("Ocorreu um erro inesperado.")
                .build();
        logError(body, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONNECTION, "close")
                .body(body);
    }

    private ErrorResponse.Builder base(HttpServletRequest req, HttpStatus status, String code) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .path(req != null ? req.getRequestURI() : "")
                .correlationId(MDC.get("correlationId"));
    }

    private void logWarn(ErrorResponse body, Exception ex) {
        org.slf4j.LoggerFactory.getLogger(WebExceptionHandler.class)
                .warn("[{}] {} {} -> {} {}",
                        body.code, body.path, body.correlationId, body.status, body.message);
    }

    private void logError(ErrorResponse body, Exception ex) {
        org.slf4j.LoggerFactory.getLogger(WebExceptionHandler.class)
                .error("[{}] {} {} -> {} {}", body.code, body.path, body.correlationId, body.status, body.message, ex);
    }

    private String safe(Object o) {
        return o == null ? null : String.valueOf(o);
    }


    public static final class ErrorResponse {
        public final String timestamp;
        public final int status;
        public final String error;
        public final String code;
        public final String message;
        public final String path;
        public final String correlationId;
        public final List<FieldViolation> fieldErrors;

        private ErrorResponse(Builder b) {
            this.timestamp = b.timestamp;
            this.status = b.status;
            this.error = b.error;
            this.code = b.code;
            this.message = b.message;
            this.path = b.path;
            this.correlationId = b.correlationId;
            this.fieldErrors = b.fieldErrors == null ? List.of() : List.copyOf(b.fieldErrors);
        }

        public static Builder builder() { return new Builder(); }

        public static final class Builder {
            private String timestamp;
            private int status;
            private String error;
            private String code;
            private String message;
            private String path;
            private String correlationId;
            private List<FieldViolation> fieldErrors;

            public Builder timestamp(String v) { this.timestamp = v; return this; }
            public Builder status(int v) { this.status = v; return this; }
            public Builder error(String v) { this.error = v; return this; }
            public Builder code(String v) { this.code = v; return this; }
            public Builder message(String v) { this.message = v; return this; }
            public Builder path(String v) { this.path = v; return this; }
            public Builder correlationId(String v) { this.correlationId = v; return this; }
            public Builder withFieldErrors(List<FieldViolation> v) { this.fieldErrors = v; return this; }
            public Builder withMessage(String v) { this.message = v; return this; }
            public ErrorResponse build() { return new ErrorResponse(this); }
        }
    }

    public record FieldViolation(String field, String message, Object rejectedValue) {}
}
