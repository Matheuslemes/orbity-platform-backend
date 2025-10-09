package br.com.orbity.customer.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxSpringRepository extends JpaRepository<OutboxJpaEntity, Long> {

    List<OutboxJpaEntity> findTop200ByPublishedFalseOrderByCreatedAtAsc();

}
