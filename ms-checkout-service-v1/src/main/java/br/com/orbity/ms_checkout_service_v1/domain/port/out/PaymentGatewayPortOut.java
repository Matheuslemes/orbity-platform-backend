package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;

public interface PaymentGatewayPortOut {

    boolean authorize(Checkout checkout); // true = authorized / false = denied

    void refund(Checkout checkout); // compensacao

}
