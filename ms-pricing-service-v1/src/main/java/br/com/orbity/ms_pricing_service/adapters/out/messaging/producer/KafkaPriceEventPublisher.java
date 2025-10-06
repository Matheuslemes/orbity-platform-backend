package br.com.orbity.ms_pricing_service.adapters.out.messaging.producer;

import br.com.orbity.ms_pricing_service.domain.event.PriceChangedEvent;
import br.com.orbity.ms_pricing_service.domain.port.out.PriceEventPublisherPortOut;
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
public class KafkaPriceEventPublisher implements PriceEventPublisherPortOut {

    private final KafkaTemplate<String, PriceChangedEvent> template;

    @Value("${pricing.kafka.producer.topics.price-changed.name}")
    private String topic;

    @Override
    public void publishChanged(PriceChangedEvent event) {
        final String key = event.sku();

        template.send(topic, key, event)
                .whenComplete((SendResult<String, PriceChangedEvent> result, Throwable ex) -> {

                    if (ex != null) {
                        log.error("[KafkaPriceEventPublisher] FAIL key={} error={}", key, ex.getMessage(), ex);
                        return;
                    }
                    if (result == null) {
                        log.warn("[KafkaPriceEventPublisher] WARN key={} result nulo", key);
                        return;
                    }

                    RecordMetadata md = result.getRecordMetadata();
                    if (md != null) {
                        log.info("[KafkaPriceEventPublisher] OK topic={} partition={} offset={} key={}",
                                md.topic(), md.partition(), md.offset(), key);
                    } else {
                        log.info("[KafkaPriceEventPublisher] OK (sem metadata) topic={} key={}", topic, key);
                    }

                });
    }
}
