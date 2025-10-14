package br.com.catalog.ms_orders_service_v1.infra;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter implements Filter {

    public static final String CORR_ID = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) req;
        String cid = http.getHeader(CORR_ID);
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();

        MDC.put(CORR_ID, cid);
        try {

            chain.doFilter(req, res);

        } finally {

            MDC.remove(CORR_ID);

        }

    }

}