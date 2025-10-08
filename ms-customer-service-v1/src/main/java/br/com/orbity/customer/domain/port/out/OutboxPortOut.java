package br.com.orbity.customer.domain.port.out;

import java.util.List;
import java.util.UUID;

public interface OutboxPortOut {

    record OutboxRecord(long id, String eventType, String payload) {}

    void append(String eventType, String payload, UUID aggregateId);

    List<OutboxRecord> fetchUnpublished(int limit);

    void markPublished(long id);
}
