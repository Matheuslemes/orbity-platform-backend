package br.com.orbity.ms_inventory_service.domain.port.out;

public interface StockEventPublisherPortOut {

    void publish(Object domainEvent);

}