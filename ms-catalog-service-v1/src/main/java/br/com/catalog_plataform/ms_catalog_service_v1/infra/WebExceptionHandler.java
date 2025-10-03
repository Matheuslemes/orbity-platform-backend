package br.com.catalog_plataform.ms_catalog_service_v1.infra;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation error");
        pd.setType(URI.create("https://http.dev/problems/validation-error"));
        pd.setProperty("path", req.getRequestURI());

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toMap).toList();
        pd.setProperty("errors", errors);

        return pd;

    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal error");
        pd.setProperty("path", req.getRequestURI());

        return pd;

    }

    private Map<String, String> toMap(FieldError fe) {

        Map<String, String> map = new HashMap<>();
        map.put("field", fe.getField());
        map.put("message", Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"));

        return map;

    }
}
