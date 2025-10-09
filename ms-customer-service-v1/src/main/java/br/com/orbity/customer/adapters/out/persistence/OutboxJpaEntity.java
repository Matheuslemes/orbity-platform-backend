package br.com.orbity.customer.adapters.out.persistence;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID aggregateId;

    private String eventType;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    private boolean published;

    private OffsetDateTime createdAt;

    private OffsetDateTime publishedAt;

}
