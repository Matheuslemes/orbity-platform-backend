package br.com.orbity.ms_search_service.adapters.in.messaging.consumer;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;
import br.com.orbity.ms_search_service.domain.port.in.IndexProductPortIn;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductChangedConsumer {

    private final ObjectMapper om;
    private final IndexProductPortIn indexProduct;

    @KafkaListener(
            id = "search-product-changed",
            topics = "${orbity.kafka.consumer.topics.product-changed.name:catalog.product.changed.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-search}",
            concurrency = "${orbity.kafka.consumer.topics.product-changed.concurrency:1}"
    )
    public void onProductChanged(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        final String json = rec.value();

        try {
            if (json == null || json.isBlank()) {
                log.warn("[KafkaProductChangedConsumer] - [onProductChanged] Mensagem vazia topic={} part={} off={} key={}",
                        rec.topic(), rec.partition(), rec.offset(), rec.key());
                ack.acknowledge();
                return;
            }

            final JsonNode node = om.readTree(json);

            final String idRaw = trim(node.path("id").asText(null));

            if (idRaw == null) {

                log.warn("[KafkaProductChangedConsumer] - [onProductChanged] id ausente topic={} off={} payload={}",
                        rec.topic(), rec.offset(), json);
                ack.acknowledge();

                return;
            }
            final UUID id = UUID.fromString(idRaw);

            final String sku = trim(node.path("sku").asText(null));
            final String name = trim(node.path("name").asText(null));
            final String description = trim(node.path("description").asText(null));

            final List<String> categories = readStringList(node.path("categories"));
            final List<String> tags       = readStringList(node.path("tags"));

            final ProductIndex doc = ProductIndex.of(
                    id, sku, name, description, categories, tags,
                    null, null, OffsetDateTime.now()
            );

            log.info("[KafkaProductChangedConsumer] - [onProductChanged] RX id={} sku={} topic={} part={} off={}",
                    id, sku, rec.topic(), rec.partition(), rec.offset());

            indexProduct.index(doc);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("[KafkaProductChangedConsumer] - [onProductChanged] FAIL topic={} off={} key={} err={}",
                    rec.topic(), rec.offset(), rec.key(), e.getMessage(), e);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }

    private List<String> readStringList(JsonNode node) {

        if (node == null || node.isMissingNode() || node.isNull()) {
            return Collections.emptyList();
        }

        try {

            return om.convertValue(node, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            log.warn("[KafkaProductChangedConsumer] - [readStringList] Lista inválida, usando []. err={}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
