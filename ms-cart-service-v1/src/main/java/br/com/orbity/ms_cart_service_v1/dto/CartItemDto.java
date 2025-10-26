package br.com.orbity.ms_cart_service_v1.dto;

import java.math.BigDecimal;

public record CartItemDto(

        String sku,
        int quantity,
        BigDecimal unitPrice,
        String currency

) { }