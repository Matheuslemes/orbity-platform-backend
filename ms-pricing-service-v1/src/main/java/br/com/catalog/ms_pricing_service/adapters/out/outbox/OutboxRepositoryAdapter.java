package br.com.catalog.ms_pricing_service.adapters.out.outbox;

import br.com.catalog.ms_pricing_service.adapters.out.persistence.OutboxJpaEntity;
import br.com.catalog.ms_pricing_service.adapters.out.persistence.OutboxSpringData;
import br.com.catalog.ms_pricing_service.domain.port.out.OutboxPortOut;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OutboxRepositoryAdapter implements OutboxPortOut {

    private final OutboxSpringData repository;

    public OutboxRepositoryAdapter(OutboxSpringData repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UUID append(String aggregateType, String aggregateid, String type, String payloadJson) {

        OutboxJpaEntity e = new OutboxJpaEntity();
        e.setAggregateId(aggregateid);
        e.setType(payloadJson);
        e.setOccurredOn(Instant.now());
        e.setPublished(false);
        e.setPublishedOn(null);
        e = repository.save(e);

        return e.getId();

    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboxRecord> fetchUnpublished(int limit) {

        var page = PageRequest.of(0, Math.max(1, limit));

        return repository.findTop200ByPublishedFalseOrderByOccurredOnAsc(page)
                .stream()
                .map(e -> new OutboxRecord(e.getId(), e.getType(), e.getPayload()))
                .toList();

    }

    @Override
    @Transactional
    public void markPublished(UUID id) {

        var e = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + id));
        e.setPublished(true);
        e.setPublishedOn(Instant.now());
        repository.save(e);

    }

}