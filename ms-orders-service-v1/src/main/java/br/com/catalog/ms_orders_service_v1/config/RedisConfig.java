package br.com.catalog.ms_orders_service_v1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties props) {

        return new LettuceConnectionFactory(props.getHost(), props.getPort());

    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory cf, ObjectMapper om) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);

        RedisSerializer<String> keySer = new StringRedisSerializer();
        RedisSerializer<Object> valSer = new GenericJackson2JsonRedisSerializer(om);

        // Strings para chaves; JSON para valores
        template.setKeySerializer(keySer);
        template.setValueSerializer(valSer);
        template.setHashKeySerializer(keySer);
        template.setHashValueSerializer(valSer);

        return template;

    }

}
