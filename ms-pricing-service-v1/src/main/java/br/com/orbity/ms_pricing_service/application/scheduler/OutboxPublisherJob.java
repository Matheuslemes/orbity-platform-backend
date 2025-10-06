package br.com.orbity.ms_pricing_service.application.scheduler;

import br.com.orbity.ms_pricing_service.domain.port.out.OutboxPortOut;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisherJob {

    private final OutboxPortOut outbox;
    private final KafkaTemplate<String, String> kafka;
    private final String topic;

    public OutboxPublisherJob(OutboxPortOut outbox, KafkaTemplate<String, String> kafka,
                              @Value("${catalog.kafka.topics.price-changed.name:price-changed}") String topic) {
        this.outbox = outbox; this.kafka = kafka; this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${catalog.outbox.poll-interval-ms:1500}")
    public void dispatch() {

        List<OutboxPortOut.OutboxRecord> batch = outbox.fetchUnpublished(200);

        for (var rec : batch) {

            var record = new ProducerRecord<>(topic, null, rec.payload());
            kafka.send((Message<?>) record).whenComplete((md, err) -> {

                if (err == null) {
                    outbox.markPublished(rec.id());
                } else {
                }
            });

        }
    }
}
