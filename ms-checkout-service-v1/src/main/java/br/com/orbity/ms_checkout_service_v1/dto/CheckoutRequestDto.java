package br.com.orbity.ms_checkout_service_v1.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckoutRequestDto(

        @NotNull
        UUID checkoutId,

        @NotNull
        UUID customerId,

        @NotNull
        List<Item> items,

        @NotNull
        Address address,

        @NotNull
        Payment payment

) {

    public record Item(UUID productId, String sku, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}

    public record Address(String street, String number, String complement, String district, String city, String state, String country, String zip) {}

    public record Payment(String method, String token, String currency) {}

}
