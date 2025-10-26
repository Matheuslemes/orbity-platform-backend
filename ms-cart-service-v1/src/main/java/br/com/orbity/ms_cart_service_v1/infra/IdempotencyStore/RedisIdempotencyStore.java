package br.com.orbity.ms_cart_service_v1.infra.IdempotencyStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisIdempotencyStore {

    private static final String DEFAULT_PREFIX = "idem";
    private static final String SEP = ":";

    private final StringRedisTemplate redis;

    public boolean markIfAbsent(String key, long ttlSeconds) {
        final String k = requireKey(key);
        final long ttl = normalizeTtl(ttlSeconds);

        try {
            Boolean ok = redis.opsForValue().setIfAbsent(k, "1", Duration.ofSeconds(ttl));
            boolean acquired = Boolean.TRUE.equals(ok);
            log.debug("[Idem] markIfAbsent key={} ttl={} acquired={}", k, ttl, acquired);
            return acquired;
        } catch (RuntimeException e) {
            log.error("[Idem] markIfAbsent failed key={} msg={}", k, e.getMessage(), e);
            throw e;
        }
    }

    public String key(String operation, String... parts) {
        StringBuilder sb = new StringBuilder(DEFAULT_PREFIX)
                .append(SEP)
                .append(normalize(operation, "op"));
        if (parts != null) {
            for (String p : parts) {
                sb.append(SEP).append(normalize(p, "na"));
            }
        }
        return sb.toString();
    }

    public boolean tryLock(String key, long ttlSeconds) {
        return markIfAbsent(key, ttlSeconds);
    }

    public void release(String key) {
        final String k = requireKey(key);
        try {
            boolean removed = Boolean.TRUE.equals(redis.delete(k));
            log.debug("[Idem] release key={} removed={}", k, removed);
        } catch (RuntimeException e) {
            log.error("[Idem] release failed key={} msg={}", k, e.getMessage(), e);
            throw e;
        }
    }

    public boolean isLocked(String key) {
        final String k = requireKey(key);
        try {
            Boolean exists = redis.hasKey(k);
            return Boolean.TRUE.equals(exists);
        } catch (RuntimeException e) {
            log.error("[Idem] isLocked failed key={} msg={}", k, e.getMessage(), e);
            throw e;
        }
    }

    public long ttlSeconds(String key) {
        final String k = requireKey(key);
        try {
            Long ttl = redis.getExpire(k);
            return ttl == null ? -2 : ttl;
        } catch (RuntimeException e) {
            log.error("[Idem] ttlSeconds failed key={} msg={}", k, e.getMessage(), e);
            throw e;
        }
    }


    private String requireKey(String key) {
        String k = Objects.toString(key, "").trim();
        if (k.isBlank()) {
            throw new IllegalArgumentException("idempotency key is required");
        }
        return k;
    }

    private long normalizeTtl(long ttlSeconds) {
        if (ttlSeconds <= 0) return 1L;
        return ttlSeconds;
    }

    private String normalize(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        if (t.isEmpty()) return fallback;
        return t.replace(':', '-').replaceAll("\\s+", "-");
    }
}
