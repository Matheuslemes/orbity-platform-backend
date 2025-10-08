package br.com.orbity.customer.domain.port.out;

public interface CustomerEventPublisherPortOut {

    void publish(Object domainEvent);

}
