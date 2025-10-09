package br.com.orbity.customer.dto;

import java.time.OffsetDateTime;

public record ConsentDto(

        boolean marketingOption,
        boolean termsAccepted,
        boolean dataProcessing,
        OffsetDateTime updatedAt

) { }