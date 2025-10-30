package br.com.orbity.customer.adapters.out.messaging.producer;

import br.com.orbity.customer.config.KafkaProperties;
import br.com.orbity.customer.domain.port.out.CustomerEventPublisherPortOut;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class KafkaCustomerEventPublisher implements CustomerEventPublisherPortOut {

    private final KafkaTemplate<Object, Object> kafka;
    private final KafkaProperties props;

    @Override
    public void publish(Object domainEvent) {

        if (domainEvent == null) return;

        // Descobre o nome do tópico a partir do topic-key "customer-events"
        String topic = props.getProducer().getTopics()
                .getOrDefault("customer-events", new KafkaProperties.ProducerTopic())
                .getName();

        if (StringUtils.isBlank(topic)) topic = "customer.events.v1";

        // Chave do evento (tenta vários getters comuns; fallback = nome da classe)
        String key = resolveKey(domainEvent);

        kafka.send(topic, key, domainEvent);

    }

    private static String resolveKey(Object ev) {

        String id = tryGetter(ev, "id");

        if (StringUtils.isBlank(id)) id = tryGetter(ev, "getId");
        if (StringUtils.isBlank(id)) id = tryGetter(ev, "aggregateId");
        if (StringUtils.isBlank(id)) id = tryGetter(ev, "getAggregateId");
        if (StringUtils.isBlank(id)) id = tryGetter(ev, "customerId");
        if (StringUtils.isBlank(id)) id = tryGetter(ev, "getCustomerId");

        return StringUtils.defaultIfBlank(id, ev.getClass().getSimpleName());

    }

    private static String tryGetter(Object target, String method) {

        try {

            Method m = target.getClass().getMethod(method);
            Object v = m.invoke(target);
            return v != null ? v.toString() : null;

        } catch (Exception ignored) {

            return null;

        }

    }

}
