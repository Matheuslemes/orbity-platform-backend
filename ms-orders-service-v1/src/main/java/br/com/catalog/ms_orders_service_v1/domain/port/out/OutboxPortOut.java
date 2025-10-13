package br.com.catalog.ms_orders_service_v1.domain.port.out;

import java.util.List;
import java.util.UUID;

public interface OutboxPortOut {

    record OutboxRec(UUID id, UUID aggregateId, String eventType, String payload) { }

    void append(String eventType, String payload, UUID aggregateId);

    List<OutboxRec> fetchUnpublished(int max);

    void markPublished(UUID id);

}
