package br.com.orbity.ms_checkout_service_v1.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryReservationConfirmedEvent(

        UUID checkoutId,
        String reason,
        OffsetDateTime occurredAt

) { }
