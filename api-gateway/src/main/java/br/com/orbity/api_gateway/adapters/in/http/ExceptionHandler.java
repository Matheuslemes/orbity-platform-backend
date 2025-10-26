package br.com.orbity.api_gateway.adapters.in.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;

public class ExceptionHandler implements ErrorWebExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ResponseStatusException rse) {
            status = rse.getStatusCode() instanceof HttpStatus s ? s : HttpStatus.valueOf(rse.getStatusCode().value());
        }

        String corr = exchange.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID);
        log.error("gateway-exception status={} corrId={} path={}", status.value(), corr, exchange.getRequest().getPath(), ex);

        var body = Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "path", String.valueOf(exchange.getRequest().getPath()),
                "correlationId", corr != null ? corr : ""
        );

        byte[] bytes = toJson(body).getBytes(StandardCharsets.UTF_8);
        var resp = exchange.getResponse();
        resp.setStatusCode(status);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        resp.getHeaders().setContentLength(bytes.length);
        return resp.writeWith(Mono.just(resp.bufferFactory().wrap(bytes)));
    }

    private String toJson(Map<String, ? extends Serializable> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escape(e.getKey())).append('"').append(':');
            Object v = e.getValue();
            if (v == null || v instanceof Number) {
                sb.append(v);
            } else {
                sb.append('"').append(escape(String.valueOf(v))).append('"');
            }
        }
        return sb.append('}').toString();
    }
    private String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }

}