package br.com.orbity.ms_cart_service_v1.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "cart.kafka", name = "enabled", havingValue = "true")
public class KafkaConfig {

    /** Admin para criação/gerenciamento (usaremos mesmo que autoCreateTopics=false). */
    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", props.getBootstrapServers()));
        return new KafkaAdmin(cfg);
    }

    /** ProducerFactory com base **exclusiva** no cart.kafka.* */
    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>();

        // bootstrap / identidade
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", props.getBootstrapServers()));
        cfg.put(ProducerConfig.CLIENT_ID_CONFIG, props.getClientId());

        // qualidade / performance
        cfg.put(ProducerConfig.ACKS_CONFIG, props.getProducer().getAcks());
        cfg.put(ProducerConfig.RETRIES_CONFIG, props.getProducer().getRetries());
        cfg.put(ProducerConfig.BATCH_SIZE_CONFIG, props.getProducer().getBatchSize());
        cfg.put(ProducerConfig.LINGER_MS_CONFIG, props.getProducer().getLingerMs());
        cfg.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, props.getProducer().getCompressionType());

        // idempotência / ordenação (opcionais)
        if (props.getProducer().getIdempotence() != null) {
            cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, props.getProducer().getIdempotence());
        }
        if (props.getProducer().getMaxInFlightPerConn() != null) {
            cfg.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, props.getProducer().getMaxInFlightPerConn());
        }
        if (props.getProducer().getDeliveryTimeoutMs() != null) {
            cfg.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, props.getProducer().getDeliveryTimeoutMs());
        }

        // serializers
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, props.getProducer().getKeySerializer());
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, props.getProducer().getValueSerializer());

        // ajustes do JsonSerializer (se valueSerializer = JsonSerializer)
        if (props.getProducer().getAddTypeHeaders() != null) {
            cfg.put("spring.json.add.type.headers", props.getProducer().getAddTypeHeaders());
        }
        if (props.getProducer().getDefaultValueType() != null && !props.getProducer().getDefaultValueType().isBlank()) {
            cfg.put("spring.json.value.default.type", props.getProducer().getDefaultValueType());
        }

        return new DefaultKafkaProducerFactory<>(cfg);
    }

    /** KafkaTemplate usado pelos publishers do ms-cart. */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        KafkaTemplate<String, Object> tpl = new KafkaTemplate<>(pf);
        // opcional: adicionar interceptors, observation, etc.
        return tpl;
    }

    /**
     * Criação de tópicos controlada por flag.
     * Se preferir criar via infra IaC, desative cart.kafka.admin.auto-create-topics.
     */
    @Bean
    @ConditionalOnProperty(prefix = "cart.kafka.admin", name = "auto-create-topics", havingValue = "true", matchIfMissing = true)
    public List<NewTopic> cartTopics(KafkaProperties props) {
        int partitions = props.getAdmin().getDefaultPartitions();
        short replicas = props.getAdmin().getDefaultReplicationFactor();

        return List.of(
                TopicBuilder.name(props.getTopics().getUpdated()).partitions(partitions).replicas(replicas).build(),
                TopicBuilder.name(props.getTopics().getMerged()).partitions(partitions).replicas(replicas).build()
        );
    }
}
