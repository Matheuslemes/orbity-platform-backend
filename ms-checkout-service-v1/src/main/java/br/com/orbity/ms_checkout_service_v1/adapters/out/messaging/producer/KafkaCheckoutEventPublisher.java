package br.com.orbity.ms_checkout_service_v1.adapters.out.messaging.producer;

import br.com.orbity.ms_checkout_service_v1.config.KafkaProperties;
import br.com.orbity.ms_checkout_service_v1.domain.event.OrderCreatedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.OrderEventPublisherPortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCheckoutEventPublisher implements OrderEventPublisherPortOut {

    private final KafkaTemplate<Object, Object> kafka;
    private final KafkaProperties props;

    @Override
    public void publishOrderCreated(OrderCreatedEvent evt) {

        String topic = props.getProducer().getTopics()
                .getOrDefault("order-created", new KafkaProperties.ProducerTopic())
                .getName();

        if (topic == null || topic.isBlank()) topic = "orders.created.v1";
        kafka.send(topic, evt.checkoutId(), evt);

    }

}
