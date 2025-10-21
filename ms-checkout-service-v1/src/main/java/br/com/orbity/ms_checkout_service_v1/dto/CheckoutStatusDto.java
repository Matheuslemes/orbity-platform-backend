package br.com.orbity.ms_checkout_service_v1.dto;

import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutStatusDto(

        UUID checkoutId,
        UUID customerId,
        CheckoutStatus status,
        BigDecimal totalAmount

) { }
