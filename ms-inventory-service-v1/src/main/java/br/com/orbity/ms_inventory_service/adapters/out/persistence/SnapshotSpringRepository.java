package br.com.orbity.ms_inventory_service.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnapshotSpringRepository extends JpaRepository<SnapshotJpaEntity, UUID> {
}
