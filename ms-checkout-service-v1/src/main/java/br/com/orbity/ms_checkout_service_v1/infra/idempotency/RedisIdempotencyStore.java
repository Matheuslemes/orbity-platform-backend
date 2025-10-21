package br.com.orbity.ms_checkout_service_v1.infra.idempotency;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
public class RedisIdempotencyStore {

    private final RedisTemplate<String, Object> redis;

    public RedisIdempotencyStore(RedisTemplate<String, Object> redis) {

        this.redis = redis;

    }

    public boolean putIfAbsent(String key, String value, long ttlSeconds){

        return Boolean.TRUE.equals(redis.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(ttlSeconds)));

    }

}
