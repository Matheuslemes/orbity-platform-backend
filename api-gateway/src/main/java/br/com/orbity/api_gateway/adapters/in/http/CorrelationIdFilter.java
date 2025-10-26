package br.com.orbity.api_gateway.adapters.in.http;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Correlation-Id";
    public static final String ATTR   = "correlationId";
    static final String CORRELATION_ID = "X-Correlation-Id";


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();

        final String existing = request.getHeaders().getFirst(HEADER);
        final String correlationId = (existing != null && !existing.isBlank())
                ? existing
                : UUID.randomUUID().toString();

        final ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER, correlationId)
                .build();

        final ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        mutatedExchange.getAttributes().put(ATTR, correlationId);

        mutatedExchange.getResponse().getHeaders().add(HEADER, correlationId);

        return chain.filter(mutatedExchange);
    }
}