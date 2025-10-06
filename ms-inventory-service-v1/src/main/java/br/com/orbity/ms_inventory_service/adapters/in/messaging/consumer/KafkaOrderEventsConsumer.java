package br.com.orbity.ms_inventory_service.adapters.in.messaging.consumer;

import br.com.orbity.ms_inventory_service.adapters.in.messaging.consumer.payload.OrderStockEventPayload;
import br.com.orbity.ms_inventory_service.domain.port.in.UpdateStockCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventsConsumer {

    private static final String HDR_EVENT_ID = "eventId";

    private final ObjectMapper mapper;

    private final UpdateStockCommand updateStock;

    @KafkaListener(
            id = "inventory-order-events-listener",
            groupId = "${spring.kafka.consumer.group-id:ms-inventory}",
            topics = "${catalog.kafka.consumer.topics.order-events.name}",
            concurrency = "${catalog.kafka.consumer.topics.order-events.concurrency:1}"
    )
    public void onOrderEvent(
            ConsumerRecord<String, String> rec,
            Acknowledgment ack,
            @Header(name = HDR_EVENT_ID, required = false) String hdrEventId
    ) {
        log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] IN -> topic={} part={} off={} key={}",
                rec.topic(), rec.partition(), rec.offset(), rec.key());
        try {

            final String value = rec.value();

            if (value == null || value.isBlank()) {

                log.warn("[KafkaOrderEventsConsumer] - [onOrderEvent] MSG-EMPTY -> topic={} part={} off={} key={}", rec.topic(), rec.partition(), rec.offset(), rec.key());
                ack.acknowledge();
                log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] OUT -> ACK (empty)");

                return;
            }

            final OrderStockEventPayload evt = mapper.readValue(value, OrderStockEventPayload.class);
            if (evt == null || !evt.isValidBasic()) {

                log.warn("[KafkaOrderEventsConsumer] - [onOrderEvent] PAYLOAD-INVALID -> off={} payload={}", rec.offset(), value);
                ack.acknowledge();
                log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] OUT -> ACK (invalid)");

                return;
            }

            final String eventId = evt.bestEffortEventId(
                    hdrEventId,
                    rec.key(),
                    rec.topic() + ":" + rec.partition() + ":" + rec.offset()
            );

            log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] RX -> eventId={} type={} orderId={} sku={} qty={} topic={} part={} off={}", eventId, evt.type(), evt.orderId(), evt.sku(), evt.qty(), rec.topic(), rec.partition(), rec.offset());

            // Roteamento por tipo (exemplos; integre com seus casos de uso/saga se desejar)
            switch (evt.type()) {

                case RESERVE -> {

                    log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] ROUTE -> RESERVE sku={} qty={}", evt.sku(), evt.qty());
                    updateStock.reserve(evt.aggregateId(), evt.qty());

                }

                case RELEASE -> {

                    log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] ROUTE -> RELEASE sku={} qty={}", evt.sku(), evt.qty());
                    updateStock.release(evt.aggregateId(), evt.qty());

                }

                case DECREMENT -> {

                    log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] ROUTE -> DECREMENT sku={} qty={}", evt.sku(), evt.qty());
                    updateStock.decrement(evt.aggregateId(), evt.qty());

                }

                case UNKNOWN -> {

                    log.warn("[KafkaOrderEventsConsumer] - [onOrderEvent] ROUTE -> UNKNOWN eventId={}", eventId);
                    ack.acknowledge();
                    log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] OUT -> ACK (unknown)");

                    return;
                }
            }

            ack.acknowledge();
            log.info("[KafkaOrderEventsConsumer] - [onOrderEvent] OUT -> ACK OK (eventId={})", eventId);

        } catch (Exception e) {

            log.error("[KafkaOrderEventsConsumer] - [onOrderEvent] FAIL -> topic={} off={} key={} err={}",
                    rec.topic(), rec.offset(), rec.key(), e.getMessage(), e);

        }

    }
}