package br.com.catalog.ms_inventory_service.domain.port.out;

public interface StockEventPublisherPortOut {

    void publish(Object domainEvent);

}