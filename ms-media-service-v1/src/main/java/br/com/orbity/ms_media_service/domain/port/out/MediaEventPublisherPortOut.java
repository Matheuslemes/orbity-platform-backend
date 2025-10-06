package br.com.orbity.ms_media_service.domain.port.out;

public interface MediaEventPublisherPortOut {

    void publish(Object domainEvent);

}
