package br.com.catalog.ms_orders_service_v1.domain.port.out;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPortOut {

    void save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByCustomerId(UUID customerId, int page, int size);

}
