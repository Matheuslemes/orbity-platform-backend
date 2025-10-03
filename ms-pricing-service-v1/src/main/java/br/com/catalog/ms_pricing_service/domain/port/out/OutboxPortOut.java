package br.com.catalog.ms_pricing_service.domain.port.out;

import java.util.List;
import java.util.UUID;

public interface OutboxPortOut {

    UUID append(String aggregateType, String aggregateid, String type, String payloadJson);

    List<OutboxRecord> fetchUnpublished(int limit);

    void markPublished(UUID id);

    record OutboxRecord(UUID id, String type, String payload) { }
}
