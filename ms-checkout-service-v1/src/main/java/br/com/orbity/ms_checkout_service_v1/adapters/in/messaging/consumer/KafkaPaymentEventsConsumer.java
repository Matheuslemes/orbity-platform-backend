package br.com.orbity.ms_checkout_service_v1.adapters.in.messaging.consumer;

import br.com.orbity.ms_checkout_service_v1.application.process.CheckoutSagaOrchestrator;
import br.com.orbity.ms_checkout_service_v1.domain.event.PaymentAuthorizedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.event.PaymentDeniedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentEventsConsumer {

    private final CheckoutSagaOrchestrator saga;

    @KafkaListener(topics = "${catalog.kafka.consumer.topics.payment-authorized.name:payment.authorized.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-checkout}")
    public void paymentAuthorized(PaymentAuthorizedEvent evt){

        saga.handlePaymentAuthorized(evt);

    }

    @KafkaListener(topics = "${catalog.kafka.consumer.topics.payment-denied.name:payment.denied.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-checkout}")
    public void paymentDenied(PaymentDeniedEvent evt){

        saga.handlePaymentDenied(evt);

    }

}
