package br.com.orbity.ms_cart_service_v1.domain.port.in;

import java.math.BigDecimal;

public record SnapshotPriceResponse(

        BigDecimal unitPrice,
        String currency

) { }
