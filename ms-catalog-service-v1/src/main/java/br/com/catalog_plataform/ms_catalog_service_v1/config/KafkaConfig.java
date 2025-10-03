package br.com.catalog_plataform.ms_catalog_service_v1.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaConfig {

    @Bean
    public DefaultKafkaProducerFactoryCustomizer producerCustomizer() {

        return factory -> {

            Map<String, Object> cfg = new HashMap<>();
            cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            cfg.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            factory.updateConfigs(cfg);

        };
    }
}
