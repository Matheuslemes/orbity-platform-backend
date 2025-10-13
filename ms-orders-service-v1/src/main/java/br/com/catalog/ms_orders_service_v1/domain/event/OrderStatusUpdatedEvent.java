package br.com.catalog.ms_orders_service_v1.domain.event;

import br.com.catalog.ms_orders_service_v1.domain.model.OrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderStatusUpdatedEvent(

        UUID orderId,
        OrderStatus status,
        OffsetDateTime occurredAt

) { }
