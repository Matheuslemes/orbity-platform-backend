package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;

import java.util.Optional;
import java.util.UUID;

public interface SagaStateRepositoryPortOut {

    void upsert(Checkout checkout);

    Optional<Checkout> byId(UUID checkoutId);

}
