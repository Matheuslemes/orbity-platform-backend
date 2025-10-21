package br.com.orbity.ms_checkout_service_v1.infra;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    public static final String HDR = "x-correlation-id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) req;
        String cid = http.getHeader(HDR);

        if (cid == null || cid.isBlank()) {

            cid = UUID.randomUUID().toString();

        }

        MDC.put("cid", cid);

        try {

            chain.doFilter(req, res);

        } finally {

            MDC.remove("cid");

        }

    }

}
