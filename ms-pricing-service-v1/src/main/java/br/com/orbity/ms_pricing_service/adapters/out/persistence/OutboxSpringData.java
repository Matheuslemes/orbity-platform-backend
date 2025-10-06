package br.com.orbity.ms_pricing_service.adapters.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxSpringData extends JpaRepository<OutboxJpaEntity, UUID> {

    List<OutboxJpaEntity> findTop200ByPublishedFalseOrderByOccurredOnAsc(Pageable pageable);

}
