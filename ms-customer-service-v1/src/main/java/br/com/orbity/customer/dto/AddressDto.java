package br.com.orbity.customer.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AddressDto(

        UUID id,
        String label,
        String street,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String country,
        String zip,
        Boolean main,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) { }

