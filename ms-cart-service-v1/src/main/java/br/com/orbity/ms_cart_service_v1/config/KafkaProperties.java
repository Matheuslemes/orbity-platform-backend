package br.com.orbity.ms_cart_service_v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "cart.kafka")
public class KafkaProperties {


    // Habilita/desabilita a publicação/consumo via Kafka no Cart.
    private boolean enabled = false;


    // Lista de brokers Kafka (host:port).
    private List<String> bootstrapServers = List.of("localhost:9092");


    // client.id padrão para produtor/consumidor.
    private String clientId = "ms-cart";

    private Admin admin = new Admin();
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();
    private Topics topics = new Topics();

    @Data
    public static class Admin {

        //Cria tópicos no startup (quando possível).
        private boolean autoCreateTopics = true;
        private Integer defaultPartitions = 3;
        private Short defaultReplicationFactor = 1;

    }

    @Data
    public static class Producer {

        //Conf. básicas do produtor.
        private String acks = "all";
        private Integer retries = 3;
        private Integer batchSize = 16384;      // bytes
        private Integer lingerMs = 5;           // ms
        private String compressionType = "lz4"; // none|gzip|snappy|lz4|zstd
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

    }

    @Data
    public static class Consumer {

        //Conf. básicas do consumidor.
        private String groupId = "ms-cart-group";
        private String autoOffsetReset = "latest"; // earliest|latest|none
        private Boolean enableAutoCommit = true;
        private Integer concurrency = 1; // @KafkaListener containers
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";

    }

    @Data
    public static class Topics {

        //Nome dos tópicos de eventos do Cart.
        private String updated = "cart.updated.v1";
        private String merged = "cart.merged.v1";
        private String checkedOut = "cart.checkedout.v1";

    }

}