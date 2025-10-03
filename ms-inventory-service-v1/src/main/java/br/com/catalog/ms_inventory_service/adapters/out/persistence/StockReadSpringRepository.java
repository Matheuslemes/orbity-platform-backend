package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReadSpringRepository extends JpaRepository<StockReadJpaEntity, String> {
}
