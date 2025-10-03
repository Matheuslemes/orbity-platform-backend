package br.com.catalog.ms_pricing_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "pricing.kafka")
public class KafkaProperties {

    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    public Producer getProducer() { return  producer; }
    public Consumer getConsumer() { return  consumer; }

    // producer (tópicos publicados por este serviço)
    public static class Producer {

        private Map<String, ProducerTopic> topics = new LinkedHashMap<>();
        public Map<String, ProducerTopic> getTopics() { return  topics; }
        public void setTopics(Map<String, ProducerTopic> topics) { this.topics = topics; }

    }

    public static class ProducerTopic {

        private String name;;
        public String getName() { return name;}
        public void setName(String name) { this.name = name; }

    }

    // consumer (se/quando este serviço consumir algo)
    public static class Consumer {
        private Map<String, ConsumerTopic> topics = new LinkedHashMap<>();
        public Map<String, ConsumerTopic> getTopics() { return  topics; }
        public void setTopics(Map<String, ConsumerTopic> topics) { this.topics = topics; }
    }

    public static class ConsumerTopic {

        private String name;
        private Integer concurrency = 1;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getConcurrency() { return concurrency; }
        public void setConcurrency(Integer concurrency) { this.concurrency = concurrency; }
    }
}