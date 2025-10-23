package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import com.fasterxml.jackson.databind.JsonNode;

public interface OutboxPortOut {

    void append(String type, JsonNode payload, Object aggregateId);

    void publishPending(); // usado pelo job

}
