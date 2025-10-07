package br.com.orbity.customer.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AddressChangedEvent(

        UUID customerId,
        UUID addressId,
        OffsetDateTime occurredAt

) { }
