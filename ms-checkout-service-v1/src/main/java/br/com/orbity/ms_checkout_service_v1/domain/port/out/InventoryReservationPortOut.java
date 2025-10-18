package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;

public interface InventoryReservationPortOut{

    boolean reserve(Checkout checkout); // true = reserved / false = denied

    void release(Checkout checkout); // compensação

}
