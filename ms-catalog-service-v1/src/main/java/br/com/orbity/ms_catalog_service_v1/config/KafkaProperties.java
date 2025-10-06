package br.com.orbity.ms_catalog_service_v1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "orbity.kafka")
public class KafkaProperties {

    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    public Producer getProducer() { return producer; }
    public Consumer getConsumer() { return consumer; }

    public static class Producer {

        private Map<String, ProducerTopic> topics = new LinkedHashMap<>();
        public Map<String, ProducerTopic> getTopics() { return topics; };
        public void setTopics(Map<String, ProducerTopic> topics) { this.topics = topics; }
    }

    public static class ProducerTopic {

        private String name;
        private String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    //  Consumer
    public static class Consumer {

        private Map<String, ConsumerTopic> topics = new LinkedHashMap<>();
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
