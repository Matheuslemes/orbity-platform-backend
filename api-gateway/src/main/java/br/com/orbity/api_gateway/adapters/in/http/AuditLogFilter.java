package br.com.orbity.api_gateway.adapters.in.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class AuditLogFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        // depois do CorrelationIdFilter
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();

        // Em WebFlux (reactive) esse método existe; mas usamos getMethod() com fallback por segurança
        final String method = Optional.ofNullable(request.getMethod())
                .map(HttpMethod::name)
                .orElse("UNKNOWN");

        final String path = request.getURI().getRawPath();
        final String correlationId = request.getHeaders().getFirst(CorrelationIdFilter.HEADER);
        final long start = System.currentTimeMillis();

        return chain.filter(exchange)
                .doOnSuccess(v -> logSuccess(exchange, method, path, correlationId, start))
                .doOnError(ex -> logError(exchange, method, path, correlationId, start, ex));
    }

    private void logSuccess(ServerWebExchange exchange,
                            String method,
                            String path,
                            String correlationId,
                            long start) {
        final HttpStatusCode status = exchange.getResponse().getStatusCode();
        final int code = status != null ? status.value() : 0;
        final long tookMs = System.currentTimeMillis() - start;

        log.info("gw audit: method={} path={} status={} tookMs={} cid={}",
                method, path, code, tookMs, correlationId);
    }

    private void logError(ServerWebExchange exchange,
                          String method,
                          String path,
                          String correlationId,
                          long start,
                          Throwable ex) {
        final HttpStatusCode status = exchange.getResponse().getStatusCode();
        final int code = status != null ? status.value() : 500;
        final long tookMs = System.currentTimeMillis() - start;

        log.warn("gw audit error: method={} path={} status={} tookMs={} cid={} ex={}",
                method, path, code, tookMs, correlationId, ex.toString());
    }
}