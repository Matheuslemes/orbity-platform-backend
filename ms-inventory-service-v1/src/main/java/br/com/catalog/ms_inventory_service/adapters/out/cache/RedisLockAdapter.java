package br.com.catalog.ms_inventory_service.adapters.out.cache;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;


@Component
public class RedisLockAdapter {

    private static final String PREFIX = "lock:inventory:sku:";
    private static final Duration DEFAULT_TTL = Duration.ofSeconds(30);

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "  return redis.call('del', KEYS[1]) " +
                    "else " +
                    "  return 0 " +
                    "end", Long.class);

    private final StringRedisTemplate redis;

    public RedisLockAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public String tryLockSku(String rawSku, Duration ttl) {

        String sku = normalizeSku(rawSku);
        String key = keyForSku(sku);
        String token = UUID.randomUUID().toString();
        Duration effectiveTtl = (ttl == null || ttl.isNegative() || ttl.isZero()) ? DEFAULT_TTL : ttl;

        Boolean ok = redis.opsForValue().setIfAbsent(key, token, effectiveTtl);
        return Boolean.TRUE.equals(ok) ? token : null;

    }


    public boolean unlockSku(String rawSku, String token) {

        String sku = normalizeSku(rawSku);
        Objects.requireNonNull(token, "token is required");
        String key = keyForSku(sku);

        try {
            Long res = redis.execute(UNLOCK_SCRIPT, Collections.singletonList(key), token);

            return res != null && res > 0;

        } catch (DataAccessException e) {

            return false;

        }
    }

    private static String normalizeSku(String sku) {
        if (sku == null) throw new IllegalArgumentException("sku is required");
        String s = sku.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("sku is blank");

        return s;

    }

    private static String keyForSku(String sku) {

        return PREFIX + sku;

    }
}