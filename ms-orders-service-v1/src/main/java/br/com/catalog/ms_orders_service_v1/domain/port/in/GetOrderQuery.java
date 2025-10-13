package br.com.catalog.ms_orders_service_v1.domain.port.in;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderQuery {

    Optional<Order> byId(UUID id);
}
