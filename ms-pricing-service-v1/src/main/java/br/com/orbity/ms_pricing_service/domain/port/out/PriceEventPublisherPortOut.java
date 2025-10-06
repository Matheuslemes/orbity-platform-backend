package br.com.orbity.ms_pricing_service.domain.port.out;

import br.com.orbity.ms_pricing_service.domain.event.PriceChangedEvent;

public interface PriceEventPublisherPortOut {

    void publishChanged(PriceChangedEvent event); // normalmente não usado direto - vamos via outbox
}
