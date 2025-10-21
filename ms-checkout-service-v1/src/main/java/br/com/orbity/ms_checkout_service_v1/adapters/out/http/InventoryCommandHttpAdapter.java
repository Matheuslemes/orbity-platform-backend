package br.com.orbity.ms_checkout_service_v1.adapters.out.http;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.InventoryReservationPortOut;
import org.springframework.stereotype.Component;

@Component
public class InventoryCommandHttpAdapter implements InventoryReservationPortOut {

    @Override
    public boolean reserve(Checkout checkout) {
        // TODO: HTTP para reserva de estoque
        return true;
    }

    @Override
    public void release(Checkout checkout) {

        // TODO: HTTP para liberar reserva

    }

}