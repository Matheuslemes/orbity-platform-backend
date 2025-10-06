package br.com.orbity.ms_pricing_service.domain.port.in;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateOrReplacePriceCommand(
        @NotBlank String sku,
        @NotNull  String currency,     // ISO 4217
        @NotNull  @DecimalMin("0.00") BigDecimal amount,
        String reason
) { }
