package br.com.orbity.customer.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Address(

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
        boolean main,
        OffsetDateTime createdAt,
        OffsetDateTime updateAt

) { }
