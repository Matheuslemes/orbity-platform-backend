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

    @KafkaListener(
            topics = "${orbity.kafka.consumer.topics.payment-authorized.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void paymentAuthorized(PaymentAuthorizedEvent evt) {

        saga.handlePaymentAuthorized(evt);

    }

    @KafkaListener(
            topics = "${orbity.kafka.consumer.topics.payment-denied.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void paymentDenied(PaymentDeniedEvent evt) {

        saga.handlePaymentDenied(evt);

    }

}
