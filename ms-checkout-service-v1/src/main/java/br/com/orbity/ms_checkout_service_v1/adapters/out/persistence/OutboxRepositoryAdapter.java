package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import br.com.orbity.ms_checkout_service_v1.domain.port.out.OutboxPortOut;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRepositoryAdapter implements OutboxPortOut {

    private final OutboxSpringData repo;

    @Override
    public void append(String type, JsonNode payload, Object aggregateId) {

        var e = new OutboxJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAggregateId(aggregateId instanceof UUID u ? u : null);
        e.setEventType(type);
        e.setPayload(payload);
        e.setCreatedAt(OffsetDateTime.now());
        e.setPublished(false);

        repo.save(e);

    }

    @Override
    public void publishPending() {

        // Simples (sem lock): em prod, prefira “claim”/update em lote
        repo.findAll().stream()
                .filter(o -> !o.isPublished())
                .forEach(o -> {

                    // TODO: publicar em Kafka conforme type/payload
                    o.setPublished(true);
                    o.setPublishedAt(OffsetDateTime.now());
                    repo.save(o);

                });

    }

}
