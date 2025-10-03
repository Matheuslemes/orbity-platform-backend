package br.com.catalog_plataform.ms_catalog_service_v1.config;

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

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(cf);

        RedisSerializer<String> keySer = new StringRedisSerializer();
        RedisSerializer<Object> valSer = new GenericJackson2JsonRedisSerializer(om);

        template.setKeySerializer(keySer);
        template.setHashKeySerializer(keySer);
        template.setValueSerializer(valSer);
        template.setHashValueSerializer(valSer);

        return template;

    }
}
