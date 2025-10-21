package br.com.orbity.ms_checkout_service_v1.dto;

public record PaymentNotificationDto(

        String eventType,
        String payload

) { }
