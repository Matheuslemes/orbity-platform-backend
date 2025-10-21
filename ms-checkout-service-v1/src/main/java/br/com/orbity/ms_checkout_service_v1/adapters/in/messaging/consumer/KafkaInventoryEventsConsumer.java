package br.com.orbity.ms_checkout_service_v1.adapters.in.messaging.consumer;

import br.com.orbity.ms_checkout_service_v1.application.process.CheckoutSagaOrchestrator;
import br.com.orbity.ms_checkout_service_v1.domain.event.InventoryReservationConfirmedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.event.InventoryReservationDeniedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KafkaInventoryEventsConsumer {

    private final CheckoutSagaOrchestrator saga;

    @KafkaListener(topics = "${catalog.kafka.consumer.topics.inventory-reserved.name:inventory.reserve.confirmed.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-checkout}")
    public void reserved(InventoryReservationConfirmedEvent evt){

        saga.handleInventoryReserved(evt);

    }

    @KafkaListener(topics = "${catalog.kafka.consumer.topics.inventory-denied.name:inventory.reserve.denied.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-checkout}")
    public void denied(InventoryReservationDeniedEvent evt){

        saga.handleInventoryDenied(evt);

    }

}
