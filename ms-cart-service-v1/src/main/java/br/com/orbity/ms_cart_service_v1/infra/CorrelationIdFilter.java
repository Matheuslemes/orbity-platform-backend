package br.com.orbity.ms_cart_service_v1.infra;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HDR_CORRELATION_ID = "X-Correlation-Id";
    public static final String HDR_REQUEST_ID     = "X-Request-Id";
    public static final String MDC_CORRELATION_ID = "correlationId";

    private static final Pattern SAFE = Pattern.compile("[^a-zA-Z0-9._-]");
    private static final int MAX_LEN = 128;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String incoming = firstNonBlank(
                request.getHeader(HDR_CORRELATION_ID),
                request.getHeader(HDR_REQUEST_ID),
                MDC.get(MDC_CORRELATION_ID)
        );

        String correlationId = normalize(incoming);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_CORRELATION_ID, correlationId);
        response.setHeader(HDR_CORRELATION_ID, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private String normalize(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        s = SAFE.matcher(s).replaceAll("-");
        if (s.length() > MAX_LEN) {
            s = s.substring(0, MAX_LEN);
        }
        return s.isEmpty() ? null : s;
    }

}
