package br.com.orbity.ms_checkout_service_v1.domain.port.in;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;

public interface StartCheckoutCommand {

    Checkout start(Checkout draft, String idempotencyKey);

}
