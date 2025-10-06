package br.com.orbity.ms_inventory_service.adapters.out.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class RedisIdempotencyStore {

    private static final String PREFIX = "idem:inventory:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(6);

    private final StringRedisTemplate redis;

    public RedisIdempotencyStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public boolean putIfAbsent(String rawKey, Duration ttl) {

        String key = buildKey(rawKey);
        Duration effectiveTtl = (ttl == null || ttl.isNegative() || ttl.isZero()) ? DEFAULT_TTL : ttl;
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", effectiveTtl);

        return Boolean.TRUE.equals(ok);

    }

    public boolean exists(String rawKey) {

        String key = buildKey(rawKey);
        Boolean has = redis.hasKey(key);

        return Boolean.TRUE.equals(has);

    }

    public String buildKey(String rawKey) {

        Objects.requireNonNull(rawKey, "rawKey is required");
        String k = rawKey.trim();

        if (k.isEmpty()) throw new IllegalArgumentException("rawKey is blank");

        return PREFIX + k;

    }
}