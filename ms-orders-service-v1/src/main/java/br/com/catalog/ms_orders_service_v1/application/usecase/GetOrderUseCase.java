package br.com.catalog.ms_orders_service_v1.application.usecase;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.port.in.GetOrderQuery;
import br.com.catalog.ms_orders_service_v1.domain.port.out.OrderRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrderUseCase implements GetOrderQuery {

    private final OrderRepositoryPortOut repository;

    @Override
    public Optional<Order> byId(UUID id) {

        log.info("[GetOrderUseCase] - [byId] IN -> id={}", id);
        return repository.findById(id);

    }
}
