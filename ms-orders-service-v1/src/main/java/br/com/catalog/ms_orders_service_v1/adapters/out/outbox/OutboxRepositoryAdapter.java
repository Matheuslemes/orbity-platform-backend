package br.com.catalog.ms_orders_service_v1.adapters.out.outbox;

import br.com.catalog.ms_orders_service_v1.domain.port.out.OutboxPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRepositoryAdapter implements OutboxPortOut {

    private final OutboxSpringData repository;

    @Override
    public void append(String eventType, String payload, UUID aggregateId) {

        log.info("[OutboxRepositoryAdapter] - [append] IN -> type={} agg={}", eventType, aggregateId);
        var e = new OutboxJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAggregateId(aggregateId);
        e.setEventType(eventType);
        e.setPayload(payload);
        e.setCreatedAt(OffsetDateTime.now());
        e.setPublished(false);
        repository.save(e);

    }

    @Override
    public List<OutboxRec> fetchUnpublished(int max) {

        var list = repository.findTop200ByPublishedOrderByCreatedAtAsc(false);
        return list.stream()
                .map(e -> new OutboxRec(e.getId(), e.getAggregateId(), e.getEventType(), e.getPayload()))
                .toList();

    }

    @Override
    public void markPublished(UUID id) {

        repository.findById(id).ifPresent(e -> {
            e.setPublished(true);
            e.setPublishedAt(OffsetDateTime.now());
            repository.save(e);
        });

    }

}
