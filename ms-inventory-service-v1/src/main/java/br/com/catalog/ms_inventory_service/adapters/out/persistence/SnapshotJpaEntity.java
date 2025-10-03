package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "snapshot_store")
@Getter @Setter
public class SnapshotJpaEntity {

    @Id
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "snapshot_state", columnDefinition = "jsonb", nullable = false)
    private String snapshotState;

    @Column(name = "taken_at", nullable = false)
    private OffsetDateTime takenAt;
}
