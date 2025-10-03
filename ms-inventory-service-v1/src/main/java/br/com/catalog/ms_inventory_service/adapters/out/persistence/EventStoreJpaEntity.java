package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_store")
@Getter @Setter
public class EventStoreJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false, length = 120)
    private String aggregateType;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "event_type", nullable = false, length = 120)
    private String eventType; // p.ex.: "StockReserved"

    @Column(name = "event_payload", columnDefinition = "jsonb", nullable = false)
    private String eventPayload;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // opcional

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

}
