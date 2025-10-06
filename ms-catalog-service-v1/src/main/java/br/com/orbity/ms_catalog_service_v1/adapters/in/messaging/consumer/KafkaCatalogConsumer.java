package br.com.orbity.ms_catalog_service_v1.adapters.in.messaging.consumer;

import br.com.orbity.ms_catalog_service_v1.adapters.out.messaging.payload.ProductChangePayload;
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
public class KafkaCatalogConsumer {

    private static final String HDR_EVENT_ID = "eventId";
    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "${orbity.kafka.consumer.topics.product-changed.name}",
            concurrency = "${orbity.kafka.consumer.topics.product-changed.concurrency:1}"
    )
    public void onProductChanged(ConsumerRecord<String, String> rec,
                                 Acknowledgment ack,
                                 @Header(name = HDR_EVENT_ID, required = false) String hdrEventId) {

        try {
            var evt = mapper.readValue(rec.value(), ProductChangePayload.class);
            var eventId = hdrEventId != null ? hdrEventId : evt.eventId();

            log.info("[KafkaCatalogConsumer] - [onProductChanged] RX id={} sku={} type={} eventId={} topic={} part={} off={}",
                    evt.id(), evt.sku(), evt.eventType(), eventId, rec.topic(), rec.partition(), rec.offset());


            ack.acknowledge();
        } catch (Exception e) {

            log.error("[KafkaCatalogConsumer] - [onProductChanged] FAIL topic={} off={} key={} err={}",
                    rec.topic(), rec.offset(), rec.key(), e.getMessage(), e);

        }
    }
}
