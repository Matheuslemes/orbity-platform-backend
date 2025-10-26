package br.com.orbity.ms_cart_service_v1.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(

        String cartId,
        List<CartItemDto> items,
        BigDecimal total

) { }

