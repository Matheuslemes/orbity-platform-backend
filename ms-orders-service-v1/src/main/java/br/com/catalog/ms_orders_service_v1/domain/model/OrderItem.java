package br.com.catalog.ms_orders_service_v1.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(

        UUID id,
        UUID productId,
        String sku,
        String name,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal

) { }
