package br.com.orbity.ms_checkout_service_v1.adapters.out.cache;

import br.com.orbity.ms_checkout_service_v1.domain.port.out.CorrelationStorePortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
public class RedisCorrelationStoreAdapter implements CorrelationStorePortOut {

    private final RedisTemplate<String, Object> redis;

    @Override
    public boolean tryLock(String key, long ttlSeconds) {

        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(ttlSeconds));

        return Boolean.TRUE.equals(ok);

    }

    @Override
    public void release(String key) {

        redis.delete(key);

    }

    @Override
    public Optional<String> get(String key) {

        Object v = redis.opsForValue().get(key);

        return Optional.ofNullable(v == null ? null : v.toString());

    }

    @Override
    public void put(String key, String value, long ttlSeconds) {

        redis.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));

    }

}
