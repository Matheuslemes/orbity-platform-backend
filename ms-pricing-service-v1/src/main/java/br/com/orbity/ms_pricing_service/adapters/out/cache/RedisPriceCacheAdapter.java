package br.com.orbity.ms_pricing_service.adapters.out.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

// utilitario básico (opitional) para set/get manual além de @Cacheable

@Component
public class RedisPriceCacheAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisPriceCacheAdapter(RedisTemplate<String, Object> redis) {
        this.redisTemplate = redis;
    }

    public void putActive(String sku, Object dto, Duration ttl) {

        String key = "price:active:" + sku;
        redisTemplate.opsForValue().set(key, dto, ttl);

    }

    public Object getActive(String sku) {

        return redisTemplate.opsForValue().get("price:active:" + sku);
    }
}
