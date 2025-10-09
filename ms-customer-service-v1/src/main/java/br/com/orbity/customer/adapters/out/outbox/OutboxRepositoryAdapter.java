package br.com.orbity.customer.adapters.out.outbox;

import br.com.orbity.customer.adapters.out.persistence.OutboxJpaEntity;
import br.com.orbity.customer.adapters.out.persistence.OutboxSpringRepository;
import br.com.orbity.customer.domain.port.out.OutboxPortOut;
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

    private final OutboxSpringRepository repository;

    @Override
    public void append(String eventType, String payload, UUID aggregateId) {

        var e = new OutboxJpaEntity();
        e.setAggregateId(aggregateId);
        e.setEventType(eventType);
        e.setPayload(payload);
        e.setCreatedAt(OffsetDateTime.now());
        e.setPublished(false);
        repository.save(e);

    }

    @Override
    public List<OutboxRecord> fetchUnpublished(int limit) {

        return repository.findTop200ByPublishedFalseOrderByCreatedAtAsc()
                .stream().limit(limit)
                .map(e -> new OutboxRecord(e.getId(), e.getEventType(), e.getPayload()))
                .toList();

    }

    @Override
    public void markPublished(long id) {

        var e = repository.findById(id).orElseThrow();
        e.setPublished(true);
        e.setPublishedAt(OffsetDateTime.now());
        repository.save(e);

    }
}
