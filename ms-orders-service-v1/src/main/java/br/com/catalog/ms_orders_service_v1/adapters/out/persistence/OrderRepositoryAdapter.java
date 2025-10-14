package br.com.catalog.ms_orders_service_v1.adapters.out.persistence;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.port.out.OrderRepositoryPortOut;
import br.com.catalog.ms_orders_service_v1.mapping.OrderJpaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPortOut {

    private final OrderSpringData repository;
    private final OrderJpaMapper mapper;

    @Override
    public void save(Order order) {

        log.info("[OrderRepositoryAdapter] - [save] IN -> id={}", order.id());
        var e = mapper.toEntity(order);
        var items = order.items().stream().map(i -> mapper.toEntityItem(i, e)).toList();
        e.setItems(items);
        repository.save(e);

    }

    @Override
    public Optional<Order> findById(UUID id) {

        log.info("[OrderRepositoryAdapter] - [findById] IN -> id={}", id);
        return repository.findById(id).map(mapper::toDomain);

    }

    @Override
    public List<Order> findByCustomerId(UUID customerId, int page, int size) {

        log.info("[OrderRepositoryAdapter] - [findByCustomerId] IN -> cid={} page={} size={}", customerId, page, size);
        return repository.findByCustomerId(customerId, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();

    }
}
