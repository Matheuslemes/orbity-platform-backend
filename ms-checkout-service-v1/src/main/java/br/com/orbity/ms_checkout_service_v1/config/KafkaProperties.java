package br.com.orbity.ms_checkout_service_v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "orbity.kafka")
@Data
public class KafkaProperties {

    private Producer producer = new Producer();

    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {

        private Map<String, ProducerTopic> topics = new LinkedHashMap<>();

    }

    @Data
    public static class ProducerTopic {

        private String name;

    }

    @Data
    public static class Consumer {

        private Map<String, ConsumerTopic> topics = new LinkedHashMap<>();

    }

    @Data
    public static class ConsumerTopic {

        private String name;
        private Integer concurrency = 1;

    }

}