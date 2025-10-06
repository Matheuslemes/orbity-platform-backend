package br.com.orbity.ms_inventory_service.adapters.out.messaging.producer;

import br.com.orbity.ms_inventory_service.config.KafkaProperties;
import br.com.orbity.ms_inventory_service.domain.event.StockAdjusted;
import br.com.orbity.ms_inventory_service.domain.event.StockDecremented;
import br.com.orbity.ms_inventory_service.domain.event.StockReleased;
import br.com.orbity.ms_inventory_service.domain.event.StockReserved;
import br.com.orbity.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaStockEventPublisher implements StockEventPublisherPortOut {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProps;

    public KafkaStockEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProps) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProps = kafkaProps;
    }

    @Override
    public void publish(Object domainEvent) {

        log.info("[KafkaStockEventPublisher] - [publish] IN -> type={}", domainEvent == null ? "null" : domainEvent.getClass().getSimpleName());

        // validações
        if (domainEvent == null) {

            log.warn("[KafkaStockEventPublisher] - [publish] IGNORE -> event null");
            return;

        }
        if (kafkaProps == null || kafkaProps.getProducer() == null) {

            log.error("[KafkaStockEventPublisher] - [publish] ERR -> KafkaProperties/Producer ausente");
            return;

        }

        // Resolve tópico
        String topic = resolveTopic(domainEvent);
        if (StringUtils.isBlank(topic)) {

            log.error("[KafkaStockEventPublisher] - [publish] ERR -> tópico ausente p/ evento={}",
                    domainEvent.getClass().getName());
            return;

        }

        // Metadados
        String eventType = eventTypeOf(domainEvent);
        String sku = skuOf(domainEvent);
        String aggregateId = aggregateIdOf(domainEvent);
        Instant occurredAt = occurredAtOf(domainEvent);

        // Valida key
        String key = StringUtils.isNotBlank(sku) ? sku : aggregateId;
        if (StringUtils.isBlank(key)) {

            log.warn("[KafkaStockEventPublisher] - [publish] KEY-MISSING -> usando eventType como key fallback");
            key = eventType;

        }

        // Monta mensagem
        Message<Object> message = MessageBuilder.withPayload(domainEvent)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader("eventType", bytes(eventType))
                .setHeader("aggregateId", bytes(aggregateId))
                .setHeader("sku", bytes(sku))
                .setHeader("occurredAt", bytes(occurredAt != null ? occurredAt.toString() : null))
                .build();

        // Envia
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);
        String finalKey = key;
        future.whenComplete((sendResult, ex) -> {
            if (ex != null) {

                log.error("[KafkaStockEventPublisher] - [publish] FAIL -> topic={} key={} type={} err={}",
                        topic, finalKey, eventType, ex.getMessage(), ex);

            } else if (sendResult != null) {

                var md = sendResult.getRecordMetadata();
                log.info("[KafkaStockEventPublisher] - [publish] OUT -> OK topic={} part={} off={} key={} type={}",
                        md.topic(), md.partition(), md.offset(), finalKey, eventType);

            } else {

                log.warn("[KafkaStockEventPublisher] - [publish] OUT -> unknown result (null)");

            }
        });
    }

    //helpers
    private String resolveTopic(Object event) {

        KafkaProperties.Producer p = kafkaProps.getProducer();
        if (p == null) return null;

        String topicKey = StringUtils.defaultIfBlank(p.getTopicKey(), "stock-events");
        Map<String, KafkaProperties.ProducerTopic> topics = p.getTopics();
        if (topics == null || topics.isEmpty()) return null;

        KafkaProperties.ProducerTopic t = topics.get(topicKey);
        return t != null ? t.getName() : null;

    }

    private static String eventTypeOf(Object e) {

        if (e instanceof StockReserved)    return "STOCK_RESERVED";
        if (e instanceof StockReleased)    return "STOCK_RELEASED";
        if (e instanceof StockDecremented) return "STOCK_DECREMENTED";
        if (e instanceof StockAdjusted)    return "STOCK_ADJUSTED";

        return e.getClass().getSimpleName();

    }

    private static String skuOf(Object e) {

        if (e instanceof StockReserved ev)     return orNull(trim(ev.sku()));
        if (e instanceof StockReleased ev)     return orNull(trim(ev.sku()));
        if (e instanceof StockDecremented ev)  return orNull(trim(ev.sku()));
        if (e instanceof StockAdjusted ev)     return orNull(trim(ev.sku()));

        return null;

    }

    private static String aggregateIdOf(Object e) {

        UUID id = null;

        if (e instanceof StockReserved ev)     id = ev.aggregateId();
        if (e instanceof StockReleased ev)     id = ev.aggregateId();
        if (e instanceof StockDecremented ev)  id = ev.aggregateId();
        if (e instanceof StockAdjusted ev)     id = ev.aggregateId();

        return id != null ? id.toString() : null;

    }

    private static Instant occurredAtOf(Object e) {

        if (e instanceof StockReserved ev)     return ev.occurredAt();
        if (e instanceof StockReleased ev)     return ev.occurredAt();
        if (e instanceof StockDecremented ev)  return ev.occurredAt();
        if (e instanceof StockAdjusted ev)     return ev.occurredAt();

        return null;

    }

    private static byte[] bytes(String s) {

        return s == null ? null : s.getBytes(StandardCharsets.UTF_8);

    }

    private static String trim(String s) {

        return s == null ? null : s.trim();

    }

    private static String orNull(String s) {

        return StringUtils.isBlank(s) ? null : s;

    }
}