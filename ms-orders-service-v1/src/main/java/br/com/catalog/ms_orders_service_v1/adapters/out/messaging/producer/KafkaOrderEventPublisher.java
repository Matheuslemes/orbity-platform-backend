package br.com.catalog.ms_orders_service_v1.adapters.out.messaging.producer;

import br.com.catalog.ms_orders_service_v1.domain.event.OrderStatusUpdatedEvent;
import br.com.catalog.ms_orders_service_v1.domain.port.out.OrderEventPublisherPortOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisherPortOut {

    private final KafkaTemplate<String, String> template;
    private final ObjectMapper om;

    @Value("${orbity.kafka.producer.topics.order-events.name:orders.status.events.v1}")
    private String topic;

    @Override
    public void publish(OrderStatusUpdatedEvent event) {

        try {
            String payload = om.writeValueAsString(event);
            template.send(topic, event.orderId().toString(), payload)
                    .whenComplete((SendResult<String, String> result, Throwable ex) -> {

                        if (ex != null) {
                            log.error("[KafkaOrderEventPublisher] FAIL key={} error={}",
                                    event.orderId(), ex.getMessage(), ex);
                            return;
                        }

                        if (result == null) {
                            log.warn("[KafkaOrderEventPublisher] WARN key={} result null", event.orderId());
                            return;
                        }

                        RecordMetadata md = result.getRecordMetadata();
                        if (md != null) {
                            log.info("[KafkaOrderEventPublisher] OK topic={} partition={} offset={} key={}",
                                    md.topic(), md.partition(), md.offset(), event.orderId());
                        } else {
                            log.info("[KafkaOrderEventPublisher] OK (sem metadata) topic={} key={}",
                                    topic, event.orderId());
                        }
                    });

        } catch (Exception e) {
            throw new IllegalStateException("serialize order event failed", e);
        }

    }

}
