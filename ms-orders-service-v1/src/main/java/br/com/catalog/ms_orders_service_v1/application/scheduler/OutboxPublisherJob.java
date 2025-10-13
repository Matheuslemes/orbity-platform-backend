package br.com.catalog.ms_orders_service_v1.application.scheduler;

import br.com.catalog.ms_orders_service_v1.domain.port.out.OutboxPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherJob {

    private final OutboxPortOut outbox;
    private final KafkaTemplate<String, String> kafka;

    @Value("${orbity.kafka.producer.topics.order-events.name:orders.status.events.v1}")
    private String topic;

    @Scheduled(fixedDelayString = "${orders.outbox.poll-interval-ms:1500}")
    public void dispatch() {

        var batch = outbox.fetchUnpublished(200);
        if (batch.isEmpty()) return;

        log.info("[OutboxPublisherJob] - [dispatch] size={}", batch.size());
        for (var rec : batch) {

            var record = new ProducerRecord<>(topic, rec.eventType(), rec.payload());
            kafka.send(record).whenComplete((md, err) -> {

                if (err == null) {
                    outbox.markPublished(rec.id());
                    if (md != null && md.getRecordMetadata() != null) {

                        var meta = md.getRecordMetadata();
                        log.info("[OutboxPublisherJob] OK topic={} part={} off={}",
                                meta.topic(), meta.partition(), meta.offset());

                    } else {

                        log.info("[OutboxPublisherJob] OK (sem metadata) topic={}", topic);

                    }
                } else {

                    log.error("[OutboxPublisherJob] FAIL send err={}", err.getMessage(), err);

                }

            });
        }

    }

}
