package br.com.orbity.ms_checkout_service_v1.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutItem(

        UUID productId,
        String sku,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTota

) { }
