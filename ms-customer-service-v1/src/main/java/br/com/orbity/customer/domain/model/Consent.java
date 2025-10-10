package br.com.orbity.customer.domain.model;

import java.time.OffsetDateTime;

public record Consent(

        boolean marketingOption,
        boolean termsAccepted,
        boolean dataProcessing,
        OffsetDateTime updatedAt

) { }
