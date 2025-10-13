package br.com.catalog.ms_orders_service_v1.adapters.in.messaging.consumer;

import br.com.catalog.ms_orders_service_v1.application.usecase.PersistOrderFromEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderCreatedConsumer {

    private final ObjectMapper om;
    private final PersistOrderFromEventUseCase persist;

    @KafkaListener(
            id = "orders-order-created",
            topics = "${orbity.kafka.consumer.topics.order-created.name:checkout.order.created.v1}",
            groupId = "${spring.kafka.consumer.gorup-id:ms-orders}",
            concurrency = "${orbity.kafka.consumer.topic.order-created.concurrency:1}"
    )
    public void onOrderCreated(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        try {

            log.info("[KafkaOrderCreatedConsumer] RX topic={} off={}", rec.topic(), rec.offset());
            persist.persistFrom(rec.value());
            ack.acknowledge();

        } catch (Exception e) {

            log.error("[KafkaOrderCreatedConsumer] FAIL topic={} off={} err-{}", rec.topic(), rec.offset(), e.getMessage(), e);

        }

    }

}
