package br.com.orbity.ms_cart_service_v1.domain.port.in;

public record RemoveItemCommand(

        String cartId,
        String sku

) { }
