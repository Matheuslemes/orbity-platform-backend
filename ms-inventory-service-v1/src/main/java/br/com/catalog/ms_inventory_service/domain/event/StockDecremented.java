package br.com.catalog.ms_inventory_service.domain.event;


import java.time.Instant;
import java.util.UUID;

public record StockDecremented(
        UUID aggregateId,
        String sku,
        long delta,
        Instant occurredAt
) implements StockEvent {

    public StockDecremented {

        if (aggregateId == null) throw new IllegalArgumentException("aggregateId is required");
        sku = StockEvent.normSku(sku);
        StockEvent.requirePositive(delta, "delta");
        occurredAt = StockEvent.normTime(occurredAt);

    }

    @Override public EventType type() {
        return EventType.STOCK_DECREMENTED;
    }

}