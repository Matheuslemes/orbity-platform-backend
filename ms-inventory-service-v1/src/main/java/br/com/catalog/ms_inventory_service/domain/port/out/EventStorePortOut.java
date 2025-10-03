package br.com.catalog.ms_inventory_service.domain.port.out;

import java.util.List;
import java.util.UUID;

public interface EventStorePortOut {


    record StoredEvent(
            long id,
            UUID aggregateId,
            long version,
            String eventType,
            String payload
    ) {}

    List<StoredEvent> loadEvents(UUID aggregateId);

    long append(UUID aggregateId, long expectedVersion, List<Object> domainEvents);
}