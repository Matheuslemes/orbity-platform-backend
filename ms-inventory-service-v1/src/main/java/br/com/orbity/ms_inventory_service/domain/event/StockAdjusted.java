package br.com.orbity.ms_inventory_service.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockAdjusted(
        UUID aggregateId,
        String sku,
        long newAvailableQty,
        Instant occurredAt
) implements StockEvent {

    public StockAdjusted {

        if (aggregateId == null) throw new IllegalArgumentException("aggregateId is required");
        sku = StockEvent.normSku(sku);
        StockEvent.requireNonNegative(newAvailableQty, "newAvailableQty");
        occurredAt = StockEvent.normTime(occurredAt);

    }

    @Override public EventType type() {
        return EventType.STOCK_ADJUSTED;
    }

}
