package br.com.orbity.ms_checkout_service_v1.domain.model;

public record PaymentInfo(

        String method,
        String token,
        String currency

) { }
