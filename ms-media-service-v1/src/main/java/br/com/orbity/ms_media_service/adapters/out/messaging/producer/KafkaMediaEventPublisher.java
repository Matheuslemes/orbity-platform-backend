package br.com.orbity.ms_media_service.adapters.out.messaging.producer;

import br.com.orbity.ms_media_service.config.KafkaProperties;
import br.com.orbity.ms_media_service.domain.port.out.MediaEventPublisherPortOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMediaEventPublisher implements MediaEventPublisherPortOut {

    private final KafkaTemplate<String, Object> kafka;
    private final KafkaProperties props;

    public KafkaMediaEventPublisher(KafkaTemplate<String, Object> kafka, KafkaProperties props) {
        this.kafka = kafka;
        this.props = props;
    }

    @Override
    public void publish(Object domainEvent) {

        log.info("[KafkaMediaEventPublisher] - [publish] IN -> type={}", domainEvent == null ? "null" : domainEvent.getClass().getSimpleName());

        if (domainEvent == null) {
            log.warn("[KafkaMediaEventPublisher] - [publish] IGNORE null event");
            return;
        }

        var p = props.getProducer();
        String topicKey = p == null ? null : p.getTopicKey();

        if (StringUtils.isBlank(topicKey)) topicKey = "media-events";
        var topic = p != null && p.getTopics() != null && p.getTopics().get(topicKey) != null
                ? p.getTopics().get(topicKey).getName() : null;

        if (StringUtils.isBlank(topic)) {

            log.error("[KafkaMediaEventPublisher] - [publish] ERR -. topic not configured (media.kafka.producer)");
            return;
        }

        Message<Object> msg = MessageBuilder.withPayload(domainEvent)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafka.send(msg).whenComplete((res, ex) -> {

            if (ex != null) {
                log.error("[KafkaMediaEventPublisher] - [publish] FAIL -> {}", ex.getMessage(), ex);
            } else {
                var md = res.getRecordMetadata();
                log.info("[KafkaMediaEventPublisher] - [publish] OUT -> topic={} part={} off={}",
                        md.topic(), md.partition(), md.offset());
            }

        });
    }
}
