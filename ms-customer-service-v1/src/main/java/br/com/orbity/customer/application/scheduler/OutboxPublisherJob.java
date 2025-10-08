package br.com.orbity.customer.application.scheduler;

import br.com.orbity.customer.domain.port.out.OutboxPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherJob {

    private final OutboxPortOut outbox;
    private final KafkaTemplate<String, String> kafka;

    @Value("${customer.kafka.topics.customer-events:customer.events.v1}")
    private String topic;

    @Scheduled(fixedDelayString = "${customer.outbox.poll-interval-ms:1500}")
    public void dispatch() {

        var batch = outbox.fetchUnpublished(200);
        if (batch.isEmpty()) return;

        log.info("[OutboxPublisherJob] - [dispatch] size={}", batch.size());
        for (var rec :  batch) {

            var record = new ProducerRecord<>(topic, rec.eventType(), rec.payload());

            kafka.send(record).whenComplete((SendResult<String, String> result, Throwable ex) -> {
                if (ex == null && result != null) {

                    var md = result.getRecordMetadata();
                    outbox.markPublished(rec.id());
                    log.info("[OutboxPublisherJob] OK topic={} part={} off={} key={} type={}",
                            md.topic(), md.partition(), md.offset(), record.key(), rec.eventType());
                } else{

                    log.error("[OutboxPublisherJob] FAIL send err={}", ex != null ? ex.getMessage() : "unknown", ex);
                }

            });
        }
    }
}
