package br.com.orbity.customer.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerDto(

        UUID id,
        String sub,
        String email,
        String firstName,
        String lastName,
        String phone,
        List<AddressDto> addresses,
        ConsentDto consent,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) { }
