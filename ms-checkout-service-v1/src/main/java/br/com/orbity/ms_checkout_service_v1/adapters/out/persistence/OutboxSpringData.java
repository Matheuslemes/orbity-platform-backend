package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxSpringData extends JpaRepository<OutboxJpaEntity, UUID> {

}
