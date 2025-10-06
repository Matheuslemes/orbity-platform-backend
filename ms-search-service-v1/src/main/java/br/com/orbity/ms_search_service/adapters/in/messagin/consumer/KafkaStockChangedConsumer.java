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
public class KafkaStockChangedConsumer {

    private final ObjectMapper om;
    private final SearchRepositoryPortOut repository;

    @KafkaListener(
            id = "search-stock-events",
            groupId = "${spring.kafka.consumer.group-id:ms-search}",
            concurrency = "${catalog.kafka.consumer.topics.stock-events.concurrency:1}"
    )
    public void onStockEvent(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        try {
            JsonNode node = om.readTree(rec.value());
            UUID id = UUID.fromString(node.path("aggregateId").asText());
            Long available = node.path("availableQty").isNumber() ? node.path("availableQty").asLong() : null;

            log.info("[KafkaStockChangedConsumer] RX id={} available={} topic={} off={}",
                    id, available, rec.topic(), rec.offset());

            repository.index(repository.findById(id).map(p -> new ProductIndex(
                    p.id(), p.sku(), p.name(), p.description(), p.categories(), p.tags(),
                    p.price(), available, p.updatedAt()
            )).orElseThrow());

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[KafkaStockChangedConsumer] FAIL topic={} off={} err={}",
                    rec.topic(), rec.offset(), e.getMessage(), e);
        }
    }
}
