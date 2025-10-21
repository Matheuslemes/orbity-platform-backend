package br.com.orbity.ms_checkout_service_v1.adapters.out.http;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.PaymentGatewayPortOut;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayHttpAdapter implements PaymentGatewayPortOut {

    @Override
    public boolean authorize(Checkout checkout) {

        // TODO: HTTP PSP
        return true;

    }

    @Override
    public void refund(Checkout checkout) {

        // TODO: HTTP PSP

    }

}
