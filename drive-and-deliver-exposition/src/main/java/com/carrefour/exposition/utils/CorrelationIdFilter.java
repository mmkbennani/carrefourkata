package com.carrefour.exposition.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements WebFilter {

    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
    public static final String CORRELATION_ID_ATTR_NAME = "correlation_id";

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse()
            .beforeCommit(this::removeCorrelationIdFromContext);
        return chain.filter(exchange)
            .contextWrite(ctx -> addCorrelationIdToContext(exchange.getRequest(), ctx));
    }

    private Context addCorrelationIdToContext(final ServerHttpRequest request, final Context context) {
        final var headers = request.getHeaders();
        return Optional.ofNullable(headers.getFirst(CORRELATION_ID_HEADER_NAME))
            .filter(StringUtils::isNotBlank)
            .filter(this::isUUID)
            .map(correlationId -> {
                MDC.put(CORRELATION_ID_ATTR_NAME, correlationId);
                return context.put(CORRELATION_ID_ATTR_NAME, correlationId);
            })
            .orElseGet(() -> {
                final var xc = UUID.randomUUID().toString();
                MDC.put(CORRELATION_ID_ATTR_NAME, xc);
                return context.put(CORRELATION_ID_ATTR_NAME, xc);
            });
    }

    private Mono<Void> removeCorrelationIdFromContext() {
        return Mono.deferContextual(Mono::just)
            .doOnNext(ctx -> MDC.remove(CORRELATION_ID_ATTR_NAME))
            .then();
    }

    private boolean isUUID(String type) {
        try {
            final var uuid = UUID.fromString(type);
            return uuid.toString().equals(type);
        } catch (IllegalArgumentException exception) {
            return Boolean.FALSE;
        }
    }
}
