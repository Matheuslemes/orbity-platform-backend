package br.com.catalog.ms_orders_service_v1.adapters.out.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxSpringData extends JpaRepository<OutboxJpaEntity, UUID> {

    List<OutboxJpaEntity> findTop200ByPublishedOrderByCreatedAtAsc(boolean published);

}
