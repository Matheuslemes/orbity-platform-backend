package br.com.catalog.ms_orders_service_v1.application.usecase;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.port.in.ListOrdersByCustomerQuery;
import br.com.catalog.ms_orders_service_v1.domain.port.out.OrderRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListOrderUseCase implements ListOrdersByCustomerQuery {

    private final OrderRepositoryPortOut repository;

    @Override
    public List<Order> listByCustomer(UUID customerId, int page, int size) {

        log.info("[ListOrderUseCase] - [listByCustomer] IN -> customerId={} page={} size={}", customerId, page, size);
        return repository.findByCustomerId(customerId, page, size);

    }
}
