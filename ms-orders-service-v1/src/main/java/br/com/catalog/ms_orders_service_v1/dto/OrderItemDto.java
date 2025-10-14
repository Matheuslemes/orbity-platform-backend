package br.com.catalog.ms_orders_service_v1.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(

        UUID id,
        UUID productId,
        String sku,
        String name,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal

) { }
