package br.com.catalog.ms_orders_service_v1.domain.port.in;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface ListOrdersByCustomerQuery {

    List<Order> listByCustomer(UUID customerId, int page, int size);

}
