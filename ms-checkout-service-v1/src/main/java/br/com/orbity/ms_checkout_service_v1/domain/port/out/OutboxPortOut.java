package br.com.orbity.ms_checkout_service_v1.domain.port.out;

public interface OutboxPortOut {

    void append(String type, String payload, Object aggregateId);

    void publishPending(); // usado pelo job

}
