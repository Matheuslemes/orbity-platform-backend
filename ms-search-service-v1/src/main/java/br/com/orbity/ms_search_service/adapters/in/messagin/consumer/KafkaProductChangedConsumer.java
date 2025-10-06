package br.com.orbity.ms_search_service.adapters.in.messagin.consumer;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;
import br.com.orbity.ms_search_service.domain.port.in.IndexProductPortIn;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductChangedConsumer {

    private final ObjectMapper om;
    private final IndexProductPortIn indexProduct;

    @KafkaListener(
            id= "search-product-changed",
            groupId = "${catalog.kafka.consumer.topics.product-changed.name",
            concurrency = "${catalog.kafka.consumer.topics.product-changed.concurrency:1}"
    )
    public void onProductChanged(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        try {
            final var json = rec.value();
            final JsonNode node = om.readTree(json);

            UUID id = UUID.fromString(node.path("id").asText());
            String sku = trim(node.path("sku").asText());
            String name = trim(node.path("name").asText());
            String description = trim(node.path("description").asText());
            var categories = om.convertValue(node.path("categories"), om.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
            var tags = om.convertValue(node.path("tags"), om.getTypeFactory()
                    .constructCollectionType(List.class, String.class));

            var doc = ProductIndex.of(id, sku, name, description, categories, tags,
                    null, null, OffsetDateTime.now());

            log.info("[KafkaProductChangedConsumer] RX product id-{} sku={} topic={] off={}",
                    id, sku, rec.topic(), rec.offset());

            indexProduct.index(doc);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("[KafkaProductChangedConsumer] FAIL topic={} off={} err={}",
                    rec.topic(), rec.offset(), e.getMessage(), e);
        }
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
