package br.com.catalog.ms_orders_service_v1.adapters.out.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class OutboxJpaEntity {

    @Id
    private UUID id;

    private UUID aggregateId;

    private String eventType;

    private String payload;

    private OffsetDateTime createdAt;

    private boolean published;

    private OffsetDateTime publishedAt;

}
