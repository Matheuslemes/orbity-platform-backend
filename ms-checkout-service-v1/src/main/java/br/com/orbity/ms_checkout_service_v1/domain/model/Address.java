package br.com.orbity.ms_checkout_service_v1.domain.model;

public record Address(

        String street,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String country,
        String zip

) { }
