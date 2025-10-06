package br.com.orbity.ms_inventory_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "catalog.kafka")
public class KafkaProperties {

    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    public Producer getProducer() { return producer; }
    public void setProducer(Producer producer) { this.producer = producer; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }

    //Producer
    public static class Producer {

        private String topicKey;

        private Map<String, ProducerTopic> topics = new LinkedHashMap<>();

        public String getTopicKey() {
            return topicKey;
        }

        public void setTopicKey(String topicKey) {
            this.topicKey = topicKey;
        }

        public Map<String, ProducerTopic> getTopics() {
            return topics;
        }

        public void setTopics(Map<String, ProducerTopic> topics) {
            this.topics = topics;
        }

    }

    public static class ProducerTopic {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    // Consumer
    public static class Consumer {

        private Map<String, ConsumerTopic> topics = new LinkedHashMap<>();

        public Map<String, ConsumerTopic> getTopics() {
            return topics;
        }

        public void setTopics(Map<String, ConsumerTopic> topics) {
            this.topics = topics;
        }

    }

    public static class ConsumerTopic {

        private String name;


        private Integer concurrency = 1;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

    }

}
