package br.com.orbity.customer.adapters.out.messaging.producer;

import br.com.orbity.customer.domain.port.out.CustomerEventPublisherPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCustomerEventPublisher implements CustomerEventPublisherPortOut {

    private final KafkaTemplate<String, Object> template;

    @Value("${customer.kafka.producer.topic-key:customer.events.v1}")
    private String topic;

    @Override
    public void publish(Object domainEvent) {

        if (domainEvent == null) {

            log.warn("[KafkaCustomerEventPublisher] Ignorando publish de evento nulo");
            return;

        }

        final String key = resolveKey(domainEvent);
        final String type = domainEvent.getClass().getSimpleName();

        template.send(topic, key, domainEvent)
                .whenComplete((SendResult<String, Object> result, Throwable ex) -> {
                    if (ex != null) {

                        log.error("[KafkaCustomerEventPublisher] FAIL key={} type={} error={}",
                                key, type, ex.getMessage(), ex);
                        return;

                    }
                    if (result == null) {

                        log.warn("[KafkaCustomerEventPublisher] WARN key={} type={} result nulo", key, type);
                        return;

                    }

                    RecordMetadata md = result.getRecordMetadata();
                    if (md != null) {

                        log.info("[KafkaCustomerEventPublisher] OK topic={} partition={} offset={} key={} type={}",
                                md.topic(), md.partition(), md.offset(), key, type);

                    } else {

                        log.info("[KafkaCustomerEventPublisher] OK (sem metadata) topic={} key={} type={}",
                                topic, key, type);
                    }

                });

    }

    // helpers
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