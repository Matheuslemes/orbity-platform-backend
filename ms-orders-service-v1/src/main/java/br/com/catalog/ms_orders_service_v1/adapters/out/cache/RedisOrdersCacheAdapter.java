package br.com.catalog.ms_orders_service_v1.adapters.out.cache;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisOrdersCacheAdapter {

    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper om;

    private String key(UUID customerId, int page, int size) {

        return "orders:customer:" + customerId + ":p" + page + ":s" + size;

    }

    public void put(UUID customerId, int page, int size, List<Order> orders, Duration ttl) {

        redis.opsForValue().set(key(customerId, page, size), orders, ttl);

    }

    @SuppressWarnings("unchecked")
    public List<Order> get(UUID customerId, int page, int size) {

        Object v = redis.opsForValue().get(key(customerId, page, size));
        if (v == null) return null;

        return om.convertValue(v, new TypeReference<List<Order>>() {});

    }

}
