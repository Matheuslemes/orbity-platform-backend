package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import br.com.orbity.ms_checkout_service_v1.domain.event.OrderCreatedEvent;

public interface OrderEventPublisherPortOut {

    void publishOrderCreated(OrderCreatedEvent evt);

}
