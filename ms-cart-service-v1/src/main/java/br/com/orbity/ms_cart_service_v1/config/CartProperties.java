package br.com.orbity.ms_cart_service_v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cart")
public class CartProperties {
    private String keyPrefix = "cart";
    private Ttl ttl = new Ttl();
    private Messaging messaging = new Messaging();
    private Redis redis = new Redis();

    @Data
    public static class Ttl {
        /**
         * TTL padrão (segundos) para o carrinho inteiro
         */
        private long seconds = 60 * 60 * 24; // 24h
    }

    @Data
    public static class Messaging {
        /**
         * Habilita publisher Kafka
         */
        private boolean enabled = false;
        private String topicUpdated = "cart.updated.v1";
        private String topicMerged = "cart.merged.v1";
        private String topicCheckedOut = "cart.checkedout.v1";
        private boolean autoCreateTopics = true;

    }

    @Data
    public static class Redis {

        private String host = "localhost";
        private int port = 6379;
        private String password;

    }

}
