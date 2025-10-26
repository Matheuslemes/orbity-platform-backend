package br.com.orbity.ms_cart_service_v1.domain.port.in;

public record AddItemCommand(

        String cartId,
        String sku,
        int quantity

) { }
