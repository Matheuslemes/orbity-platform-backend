package br.com.orbity.ms_checkout_service_v1.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CheckoutStartedEvent(

        UUID checkoutId,
        UUID customerId,
        OffsetDateTime occurredAt

) { }
