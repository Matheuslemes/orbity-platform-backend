package br.com.catalog.ms_orders_service_v1.adapters.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderSpringData extends JpaRepository<OrderJpaEntity, UUID> {

    List<OrderJpaEntity> findByCustomerId(UUID customerId, Pageable pageable);

}
