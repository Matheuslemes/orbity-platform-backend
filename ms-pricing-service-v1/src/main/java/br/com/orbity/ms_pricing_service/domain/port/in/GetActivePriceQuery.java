package br.com.orbity.ms_pricing_service.domain.port.in;

import jakarta.validation.constraints.NotBlank;

public record GetActivePriceQuery(
        @NotBlank String sku) {
}
