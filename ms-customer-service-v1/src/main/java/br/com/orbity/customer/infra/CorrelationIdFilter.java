package br.com.orbity.customer.infra;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String HDR = "x-Correlation-Id";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        var http = (HttpServletRequest) servletRequest;
        String cid = http.getHeader(HDR);

        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        MDC.put("cid", cid);

        try { filterChain.doFilter(servletRequest, servletResponse); }
        finally { MDC.remove("cid"); }

    }
}
