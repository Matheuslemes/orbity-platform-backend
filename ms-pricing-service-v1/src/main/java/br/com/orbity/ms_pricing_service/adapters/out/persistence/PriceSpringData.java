package br.com.orbity.ms_pricing_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PriceSpringData extends JpaRepository<PriceJpaEntity, UUID> {

    Optional<PriceJpaEntity> findBySku(String sku);

}
