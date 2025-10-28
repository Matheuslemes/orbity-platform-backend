package br.com.orbity.ms_cart_service_v1.adapters.out.messaging;

import br.com.orbity.ms_cart_service_v1.config.KafkaProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cart.kafka", name = "enabled", havingValue = "true")
public class KafkaCartEventPublisher implements CartEventPublisherPortOut {

    private static final String SRC = "ms-cart";
    private static final String SCHEMA = "cart-event-payload.v1";
    private static final byte[] APP_JSON = "application/json".getBytes(StandardCharsets.UTF_8);

    private final KafkaTemplate<String, Object> kafka; // agora <String, Object> (JsonSerializer)
    private final KafkaProperties props;

    @Override
    public void publishUpdated(String cartId, Cart cart, String reason) {
        final String topic = nonEmpty(props.getTopics().getUpdated(), "cart.updated.v1");
        final long ts = System.currentTimeMillis();
        final Payload payload = new Payload("UPDATED", cartId, cart, reason, ts);
        send(topic, cartId, reason, payload);
    }

    @Override
    public void publishMerged(String from, String to, Cart merged) {
        final String topic = nonEmpty(props.getTopics().getMerged(), "cart.merged.v1");
        final long ts = System.currentTimeMillis();
        final String meta = "from=" + from;
        final Payload payload = new Payload("MERGED", to, merged, meta, ts);
        send(topic, to, meta, payload);
    }


    private void send(String topic, String key, String reasonOrMeta, Payload payload) {
        final String k = requireKey(key);
        final ProducerRecord<String, Object> record = buildRecord(topic, k, reasonOrMeta, payload);

        kafka.send(record).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KafkaCartEventPublisher] send failed topic={} key={} type={} msg={}",
                        topic, k, payload.type(), ex.getMessage(), ex);
            } else if (result != null) {
                var md = result.getRecordMetadata();
                log.info("[KafkaCartEventPublisher] sent topic={} key={} type={} partition={} offset={}",
                        md.topic(), k, payload.type(), md.partition(), md.offset());
            }
        });
    }

    private ProducerRecord<String, Object> buildRecord(String topic, String key, String reasonOrMeta, Payload payload) {
        ProducerRecord<String, Object> rec = new ProducerRecord<>(topic, key, payload);

        putHeader(rec, "event-type", payload.type());
        putHeader(rec, "event-reason", reasonOrMeta);
        putHeader(rec, "cart-id", payload.cartId());
        putHeader(rec, "event-ts-epoch-ms", Long.toString(payload.tsEpochMs()));
        putHeader(rec, "content-type", APP_JSON);
        putHeader(rec, "event-schema", SCHEMA);
        putHeader(rec, "event-source", SRC);

        String corr = MDC.get("correlationId");
        if (corr != null && !corr.isBlank()) {
            putHeader(rec, "correlation-id", corr);
        }
        return rec;
    }

    private void putHeader(ProducerRecord<String, ?> rec, String name, String value) {
        if (value == null) return;
        rec.headers().add(new RecordHeader(name, value.getBytes(StandardCharsets.UTF_8)));
    }

    private void putHeader(ProducerRecord<String, ?> rec, String name, byte[] value) {
        if (value == null) return;
        rec.headers().add(new RecordHeader(name, value));
    }

    private String nonEmpty(String v, String fallback) {
        return (v == null || v.isBlank()) ? fallback : v;
    }

    private String requireKey(String key) {
        String k = Objects.toString(key, "").trim();
        if (k.isBlank()) throw new IllegalArgumentException("Kafka key (cartId) is required");
        return k;
    }

    /** Payload enxuta e estável. */
    public record Payload(
            String type,
            String cartId,
            Cart cart,
            String meta,
            long tsEpochMs
    ) { }
}
