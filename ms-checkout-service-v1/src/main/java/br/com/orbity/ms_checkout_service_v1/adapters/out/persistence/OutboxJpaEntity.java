package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter @Setter
public class OutboxJpaEntity {

    @Id
    private UUID id;

    private UUID aggregateId;

    private String eventType;

    @Lob
    private String payload;

    private OffsetDateTime createdAt;

    private boolean published;

    private OffsetDateTime publishedAt;

}