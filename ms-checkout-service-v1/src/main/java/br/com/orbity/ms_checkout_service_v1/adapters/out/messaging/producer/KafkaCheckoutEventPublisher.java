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

        var topicKey = props.getProducer().getTopicKey();
        var topicName = props.getProducer().getTopics()
                .getOrDefault(topicKey, new KafkaProperties.ProducerTopic()).getName();

        if (topicName == null || topicName.isBlank()) {
            topicName = "order.created.v1";
        }

        kafka.send(topicName, evt.checkoutId(), evt);

    }

}
