package br.com.catalog.ms_media_service.adapters.out.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisMediaCacheAdapter {

    private final StringRedisTemplate redis;

    public void cacheUrl(UUID id, String url, Duration ttl) {

        if (id == null || url == null || url.isBlank()) return;
        redis.opsForValue().set(key(id), url, ttl);
    }

    public Optional<String> findUrl(UUID id) {

        String v = redis.opsForValue().get(key(id));
        return Optional.ofNullable(v);
    }

    private String key(UUID id) { return "media:url:" + id; }
}
