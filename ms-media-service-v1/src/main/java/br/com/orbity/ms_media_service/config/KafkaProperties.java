package br.com.orbity.ms_media_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "media.kafka")
public class KafkaProperties {

    private Producer producer = new Producer();

    public Producer getProducer() { return producer; }
    public void setProducer(Producer producer) { this.producer = producer; }

    public static class Producer {

        private String topicKey; // ex.: "media-events"

        private Map<String, ProducerTopic> topics = new LinkedHashMap<>();

        public String getTopicKey() { return topicKey; }

        public void setTopicKey(String topicKey) { this.topicKey = topicKey; }

        public Map<String, ProducerTopic> getTopics() { return topics; }

        public void setTopics(Map<String, ProducerTopic> topics) { this.topics = topics; }

    }

    public static class ProducerTopic {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}