package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventStoreSpringRepository extends JpaRepository<EventStoreJpaEntity, Long> {

    List<EventStoreJpaEntity> findByAggregateIdOrderByVersionAsc(UUID aggregateId);

    @Query("select max(e.version) from EventStoreJpaEntity e where e.aggregateId = :agg")
    Long findMaxVersion(@Param("agg") UUID aggregateId);

    Optional<EventStoreJpaEntity> findTopByAggregateIdOrderByVersionDesc(UUID aggregateId);

}