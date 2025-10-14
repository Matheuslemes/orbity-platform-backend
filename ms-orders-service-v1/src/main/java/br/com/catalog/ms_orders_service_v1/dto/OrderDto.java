package br.com.catalog.ms_orders_service_v1.dto;

import br.com.catalog.ms_orders_service_v1.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto(

        UUID id,
        UUID customerId,
        BigDecimal totalAmount,
        String currency,
        OrderStatus status,
        List<OrderItemDto> items,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) { }
