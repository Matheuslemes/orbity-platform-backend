package br.com.orbity.ms_catalog_service_v1.adapters.out.messaging.producer;

import br.com.orbity.ms_catalog_service_v1.adapters.out.messaging.payload.ProductChangePayload;
import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductEventPublisherPortOut;
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
public class KafkaProductEventPublisher implements ProductEventPublisherPortOut {

    private final KafkaTemplate<String, ProductChangePayload> template;

    @Value("${catalog.kafka.producer.topic-key:product-changed}")
    private String topic;

    @Override
    public void publishChanged(Product product, Type type) {

        ProductChangePayload payload = ProductChangePayload.from(product, type.name());

        template.send(topic, product.id().toString(), payload)

                .whenComplete((SendResult<String, ProductChangePayload> result, Throwable ex) -> {
                    if (ex != null) {

                        log.error("[KafkaProductEventPublisher] FAIL key={} error={}",
                                product.id(), ex.getMessage(), ex);
                        return;
                    }
                    if (result == null) {

                        log.warn("[KafkaProductEventPublisher] WARN key={} result nulo", product.id());
                        return;
                    }

                    RecordMetadata md = result.getRecordMetadata();
                    if (md != null) {

                        log.info("[KafkaProductEventPublisher] OK topic={} partition={} offset={} key={}",
                                md.topic(), md.partition(), md.offset(), product.id());
                    } else {

                        log.info("[KafkaProductEventPublisher] OK (sem metadata) topic={} key={}",
                                topic, product.id());
                    }
                });
    }

}
