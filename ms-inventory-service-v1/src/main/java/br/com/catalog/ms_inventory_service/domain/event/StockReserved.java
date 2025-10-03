package br.com.catalog.ms_inventory_service.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockReserved(
        UUID aggregateId,
        String sku,
        long quantity,
        Instant occurredAt
) implements StockEvent {

    public StockReserved {

        if (aggregateId == null) throw new IllegalArgumentException("aggregateId is required");
        sku = StockEvent.normSku(sku);
        StockEvent.requirePositive(quantity, "quantity");
        occurredAt = StockEvent.normTime(occurredAt);

    }

    @Override public EventType type() {
        return EventType.STOCK_RESERVED;
    }
}
