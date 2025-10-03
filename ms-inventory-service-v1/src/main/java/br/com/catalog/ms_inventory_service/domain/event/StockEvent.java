package br.com.catalog.ms_inventory_service.domain.event;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public sealed interface StockEvent
        permits StockAdjusted, StockDecremented, StockReleased, StockReserved {

    UUID aggregateId();
    String sku();
    Instant occurredAt();
    EventType type();

    default String eventType() {
        return type().name();
    }

    enum EventType {
        STOCK_ADJUSTED,
        STOCK_DECREMENTED,
        STOCK_RELEASED,
        STOCK_RESERVED;

        public static EventType from(String raw) {
            if (raw == null) return null;
            return EventType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        }
    }

    // Helpers comuns
    static String normSku(String s) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("sku is required");
        return s.trim();
    }

    static Instant normTime(Instant t) { return t != null ? t : Instant.now(); }

    static void requirePositive(long v, String name) {
        if (v <= 0) throw new IllegalArgumentException(name + " must be > 0");
    }

    static void requireNonNegative(long v, String name) {
        if (v < 0) throw new IllegalArgumentException(name + " must be >= 0");
    }
}