package br.com.catalog.ms_inventory_service.adapters.in.messaging.consumer.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderStockEventPayload(

        String eventId,
        String eventType,
        String orderId,
        UUID aggregateId,
        String sku,
        Long qty,
        OffsetDateTime occurredAt

) {

    @JsonCreator
    public OrderStockEventPayload(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("aggregateId") UUID aggregateId,
            @JsonProperty("sku") String sku,
            @JsonProperty("qty") Long qty,
            @JsonProperty("occurredAt") OffsetDateTime occurredAt
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.aggregateId = aggregateId;
        this.sku = sku;
        this.qty = qty;
        this.occurredAt = occurredAt;

    }

    public EventType type() {

        return EventType.from(eventType);

    }

    public String bestEffortEventId(String... fallbacks) {

        if (!isBlank(eventId)) return eventId;
        for (String f : fallbacks) if (!isBlank(f)) return f;
        return null;

    }

    public boolean isValidBasic() {

        return !isBlank(sku) && qty != null && qty > 0 && type() != EventType.UNKNOWN;

    }

    private static boolean isBlank(String s) {

        return s == null || s.isBlank();

    }

    public enum EventType {

        RESERVE, RELEASE, DECREMENT, UNKNOWN;
        public static EventType from(String raw) {
            if (raw == null) return UNKNOWN;
            return switch (raw.trim().toUpperCase(Locale.ROOT)) {
                case "RESERVE" -> RESERVE;
                case "RELEASE" -> RELEASE;
                case "DECREMENT" -> DECREMENT;
                default -> UNKNOWN;
            };

        }

    }
}