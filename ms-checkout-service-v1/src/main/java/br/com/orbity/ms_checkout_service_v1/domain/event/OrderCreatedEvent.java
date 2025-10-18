package br.com.orbity.ms_checkout_service_v1.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderCreatedEvent(

        UUID checkoutId,
        UUID orderId,
        OffsetDateTime occurredAt

) { }
