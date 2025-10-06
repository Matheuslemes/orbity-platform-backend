package br.com.orbity.ms_pricing_service.dto;

import java.math.BigDecimal;

public record PriceDto(
        String sku,
        String currency,
        BigDecimal amount,
        boolean active
) { }
