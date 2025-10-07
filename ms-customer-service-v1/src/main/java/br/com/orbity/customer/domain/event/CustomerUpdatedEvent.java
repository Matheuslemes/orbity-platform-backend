package br.com.orbity.customer.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerUpdatedEvent(

        UUID customerId,
        String email,
        OffsetDateTime occurredAt

) { }
