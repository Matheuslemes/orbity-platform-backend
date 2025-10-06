package br.com.orbity.ms_inventory_service.adapters.out.persistence;

import br.com.orbity.ms_inventory_service.domain.port.out.EventStorePortOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class EventStoreRepositoryAdapter implements EventStorePortOut {

    private final EventStoreSpringRepository repo;
    private final ObjectMapper om;

    public EventStoreRepositoryAdapter(EventStoreSpringRepository repo, ObjectMapper om) {
        this.repo = repo;
        this.om = om;
    }

    @Override
    public List<EventStorePortOut.StoredEvent> loadEvents(UUID aggregateId) {

        return repo.findByAggregateIdOrderByVersionAsc(aggregateId)
                .stream()
                .map(e -> new EventStorePortOut.StoredEvent(
                        e.getId(),
                        e.getAggregateId(),
                        e.getVersion(),
                        e.getEventType(),
                        e.getEventPayload()
                ))
                .toList();

    }

    @Override
    @Transactional
    public long append(UUID aggregateId, long expectedVersion, List<Object> domainEvents) {

        Long current = repo.findMaxVersion(aggregateId);
        long currentVersion = current == null ? 0L : current;

        if (currentVersion != expectedVersion) {
            throw new IllegalStateException("Concurrency error: expected=" + expectedVersion + " actual=" + currentVersion);
        }

        long next = currentVersion;
        for (Object ev : domainEvents) {

            var entity = new EventStoreJpaEntity();
            entity.setAggregateId(aggregateId);
            entity.setAggregateType("StockAggregate");
            entity.setVersion(++next);
            entity.setEventType(ev.getClass().getSimpleName());

            try {
                entity.setEventPayload(om.writeValueAsString(ev));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to serialize domain event " + ev.getClass().getName(), e);
            }

            entity.setOccurredAt(OffsetDateTime.now());

            repo.save(entity);

            log.debug("Appended {} v{} for {}", entity.getEventType(), entity.getVersion(), aggregateId);

        }

        return next;

    }
}