package br.com.catalog.ms_orders_service_v1.domain.port.out;

import br.com.catalog.ms_orders_service_v1.domain.event.OrderStatusUpdatedEvent;

public interface OrderEventPublisherPortOut {

    void publish(OrderStatusUpdatedEvent event);

}
