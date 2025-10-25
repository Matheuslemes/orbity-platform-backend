package br.com.orbity.ms_cart_service_v1.config;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory(RedisProperties props, Environment env) {

        var standalone = new RedisStandaloneConfiguration(props.getHost(), props.getPort());

        if (props.getUsername() != null && !props.getUsername().isBlank()) {
            standalone.setUsername(props.getUsername());
        }

        if (props.getPassword() != null && !props.getPassword().isBlank()) {
            standalone.setPassword(RedisPassword.of(props.getPassword()));
        }

        // getDatabase() é int
        standalone.setDatabase(props.getDatabase());

        // Builder do Lettuce
        LettuceClientConfiguration.LettuceClientConfigurationBuilder client =
                LettuceClientConfiguration.builder();

        // 1) SSL via property (compatível com versões onde não há props.isSsl())
        boolean ssl = env.getProperty("spring.data.redis.ssl", Boolean.class, false);
        if (ssl) {
            client.useSsl();
        }

        // 2) Timeout (se definido)
        Duration timeout = props.getTimeout();
        if (timeout != null) {
            client.commandTimeout(timeout);
        }

        return new LettuceConnectionFactory(standalone, client.build());

    }

    @Bean
    public RedisTemplate<String, Cart> cartRedisTemplate(RedisConnectionFactory connectionFactory) {

        var template = new RedisTemplate<String, Cart>();
        template.setConnectionFactory(connectionFactory);

        var keySerializer = new StringRedisSerializer();

        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        var valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;

    }

}
