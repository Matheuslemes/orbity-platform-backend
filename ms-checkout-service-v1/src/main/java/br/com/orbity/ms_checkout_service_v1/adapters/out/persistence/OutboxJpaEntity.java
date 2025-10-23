package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter @Setter
public class OutboxJpaEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 120)
    private String eventType;

    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode payload;

    @Column(name = "created_at", columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "published_at", columnDefinition = "timestamptz")
    private OffsetDateTime publishedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
