package br.com.orbity.ms_inventory_service.dto;

import java.util.UUID;

public record StockDto(
        UUID id,
        String sku,
        long availableQty,
        long reservedQty
) {}