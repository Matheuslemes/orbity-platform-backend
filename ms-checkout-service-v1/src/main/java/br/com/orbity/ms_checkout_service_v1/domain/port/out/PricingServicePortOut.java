package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;

import java.math.BigDecimal;

public interface PricingServicePortOut {

    BigDecimal recomputeTotal(Checkout checkout);

}
