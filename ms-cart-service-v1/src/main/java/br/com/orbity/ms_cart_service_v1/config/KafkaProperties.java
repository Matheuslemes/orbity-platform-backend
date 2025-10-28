package br.com.orbity.ms_cart_service_v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "cart.kafka")
public class KafkaProperties {

    /** Habilita/desabilita a publicação/consumo via Kafka no Cart. */
    private boolean enabled = false;

    /** Lista de brokers Kafka (host:port). */
    private List<String> bootstrapServers = List.of("127.0.0.1:9094");

    /** client.id padrão para produtor/consumidor. */
    private String clientId = "ms-cart";

    private Admin admin = new Admin();
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();   // mantido para futura expansão
    private Topics topics = new Topics();

    @Data
    public static class Admin {
        /** Cria tópicos no startup (quando possível). */
        private boolean autoCreateTopics = true;
        private Integer defaultPartitions = 1;
        private Short defaultReplicationFactor = 1;
    }

    @Data
    public static class Producer {
        /** Conf. básicas do produtor. */
        private String acks = "all";
        private Integer retries = 3;
        private Integer batchSize = 16384;      // bytes
        private Integer lingerMs = 5;           // ms
        private String compressionType = "lz4"; // none|gzip|snappy|lz4|zstd

        /** Serializers (defaults coerentes com o template do publisher). */
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.springframework.kafka.support.serializer.JsonSerializer";

        /** Hardening JSON (opcionais, mas recomendados). */
        private Boolean addTypeHeaders = false;
        private String  defaultValueType = ""; // p.ex.: fully-qualified da Payload (opcional)
        private Boolean idempotence = true;    // habilita exactly-once no produtor (se fizer sentido)
        private Integer maxInFlightPerConn = 1;
        private Integer deliveryTimeoutMs = 120000; // 120s
    }

    @Data
    public static class Consumer {
        /** Conf. básicas do consumidor (mantido para uso futuro). */
        private String groupId = "ms-cart-group";
        private String autoOffsetReset = "latest"; // earliest|latest|none
        private Boolean enableAutoCommit = true;
        private Integer concurrency = 1; // @KafkaListener containers
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    }

    @Data
    public static class Topics {
        /** Nome dos tópicos de eventos do Cart. */
        private String updated = "cart.updated.v1";
        private String merged  = "cart.merged.v1";
    }
}
