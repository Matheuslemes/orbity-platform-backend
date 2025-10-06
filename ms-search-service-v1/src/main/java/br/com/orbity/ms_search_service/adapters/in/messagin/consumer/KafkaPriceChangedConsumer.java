package br.com.orbity.ms_search_service.adapters.in.messagin.consumer;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;
import br.com.orbity.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPriceChangedConsumer {

    private final ObjectMapper om;
    private final SearchRepositoryPortOut repository;

    @KafkaListener(
            id = "search-price-changed",
            groupId = "${spring.kafka.consumer.group-id:ms-search}",
            topics = "${catalog.kafka.consumer.topics.price-changed.name}",
            concurrency = "${catalog.kafka.consumer.topics.price-changed.concurrency:1}"
    )
    public void onPriceChanged(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        try {
            JsonNode node = om.readTree(rec.value());
            UUID id = UUID.fromString(node.path("productId").asText());
            Double price = node.path("newPrice").isNumber() ? node.path("newPrice").asDouble() : null;

            log.info("[KafkaPriceChangedConsumer] RX id={} price={} topic={} off={}",
                    id, price, rec.topic(), rec.offset());

            // atualização parcial via adapter (script/partial  update)
            repository.index(repository.findById(id).map(p -> new ProductIndex(
                    p.id(), p.sku(), p.name(), p.description(), p.categories(), p.tags(),
                    price, p.availableQty(), p.updatedAt()
            )).orElseThrow());

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[KafkaPriceChangedConsumer] FAIL topic={} off={} err={}",
                    rec.topic(), rec.offset(), e.getMessage());
        }
    }
}
