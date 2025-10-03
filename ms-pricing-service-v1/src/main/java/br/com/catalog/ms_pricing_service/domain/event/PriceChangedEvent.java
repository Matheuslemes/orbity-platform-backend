package br.com.catalog.ms_pricing_service.domain.event;

import java.time.Instant;

// evento de dominio serializado para outbox
public record PriceChangedEvent(
        String sku,
        String currency,
        long amountCents,
        Instant occurredOn,
        String reason
){ }
