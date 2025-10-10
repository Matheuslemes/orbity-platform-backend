package br.com.orbity.customer.adapters.out.cache;

import br.com.orbity.customer.domain.model.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCustomerCacheAdapter {

    private static final String K_ID = "cust:byId:%s";
    private static final String K_EMAIL = "cust:byEmail:%s";

    private final StringRedisTemplate redis;
    private final ObjectMapper om;

    public Optional<Customer> getById(UUID id) {

        log.info("[RedisCustomerCacheAdapter] - [getById] IN -> id={}", id);
        if (id == null) {

            log.warn("[RedisCustomerCacheAdapter] - [getById] id null");
            return Optional.empty();

        }
        final String key = keyById(id);

        try {

            String json = redis.opsForValue().get(key);
            if (StringUtils.isBlank(json)) return Optional.empty();
            return Optional.of(deserialize(json));

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [getById] FAIL id={} err={}", id, e.getMessage(), e);
            return Optional.empty();

        }

    }

    public Optional<Customer> getByEmail(String email) {

        final String norm = normalizeEmail(email);
        log.info("[RedisCustomerCacheAdapter] - [getByEmail] IN -> email={}", norm);

        if (norm == null) {

            log.error("[RedisCustomerCacheAdapter] - [geByEmail] email inválido");
            return Optional.empty();

        }

        final String key = keyByEmail(norm);
        try {

            String json = redis.opsForValue().get(key);
            if (StringUtils.isBlank(json)) return Optional.empty();
            return Optional.of(deserialize(json));

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [getByEmail] FAIL email={} err={}", norm, e.getMessage(), e);
            return Optional.empty();

        }

    }

    public void put(Customer c, Duration ttl) {

        log.info("[RedisCustomerCacheAdapter] - [put] IN -> id={} email={} ttl={}",
                 c != null ? c.getId() : null, c != null ? c.getEmail() : null, ttl);

        if (c == null || c.getId() == null) {

            log.warn("[RedisCustomerCacheAdapter] - [put] customer/id inválidos");
            return;
        }

        final String json;
        try {

            json = serialize(c);

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [put] FAIL serialize id={} err={}", c.getId(), e.getMessage(), e);
            return;

        }

        try {

            redis.opsForValue().set(keyById(c.getId().value()), json, safeTtl(ttl));

            final String normEmail = normalizeEmail(c.getEmail());
            if (normEmail != null) {

                redis.opsForValue().set(keyByEmail(normEmail), json, safeTtl(ttl));

            }

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [put] FAIL set id={} err={}", c.getId(), e.getMessage(), e);

        }

    }

    public void evictById(UUID id) {

        log.info("[RedisCustomerCacheAdapter] - [evictById] IN -> id={}", id);
        if (id == null) {

            log.warn("[RedisCustomerCacheAdapter] - [evictById] id null");
            return;

        }

        try {

            redis.delete(keyById(id));

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [evictById] FAIL id={} err={}", id, e.getMessage(), e);

        }

    }

    public void evictByEmail(String email) {

        final String norm = normalizeEmail(email);
        log.info("[RedisCustomerCacheAdapter] - [evictByEmail] IN -> email={}", norm);

        if (norm == null) {

            log.warn("[RedisCustomerCacheAdapter] - [evictByEmail] email inválido");
            return;

        }

        try {

            redis.delete(keyByEmail(norm));

        } catch (Exception e) {

            log.error("[RedisCustomerCacheAdapter] - [evictByEmail] FAIL email={} err={}", norm, e.getMessage(), e);

        }

    }

    // helpers
    private String keyById(UUID id) {

        return String.format(K_ID, id);

    }

    private String keyByEmail(String emailNorm) {

        return String.format(K_EMAIL, emailNorm);

    }

    private Duration safeTtl(Duration ttl) {

        return (ttl == null || ttl.isNegative() || ttl.isZero()) ? Duration.ofMinutes(10) : ttl;

    }

    private String serialize(Customer c) throws Exception {

        return om.writeValueAsString(c);

    }

    private Customer deserialize(String json) throws Exception {

        return om.readValue(json, new TypeReference<Customer>() {});

    }

    private String normalizeEmail(String email) {

        if (email == null) return null;
        String e = email.trim().toLowerCase();
        return e.isEmpty() ? null : e;

    }
}
