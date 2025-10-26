package br.com.orbity.ms_cart_service_v1.adapters.out.cache;

import br.com.orbity.ms_cart_service_v1.config.CartProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCartRepositoryAdapter implements CartRepositoryPortOut {

    private final RedisTemplate<String, Cart> redis;
    private final CartProperties props;

    private String norm(String s) {
        return s == null ? null : s.trim();
    }

    private String key(String cartId) {
        String prefix = props.getKeyPrefix();
        if (prefix == null || prefix.isBlank()) prefix = "cart";
        return prefix + ":" + norm(cartId);
    }

    @Override
    public Optional<Cart> findById(String cartId) {
        final String id = norm(cartId);
        if (id == null || id.isBlank()) {
            log.warn("[RedisCartRepositoryAdapter] findById chamado com cartId vazio");
            return Optional.empty();
        }

        try {
            ValueOperations<String, Cart> ops = redis.opsForValue();
            Cart cart = ops.get(key(id));
            log.debug("[RedisCartRepositoryAdapter] GET cartId={} found={}", id, cart != null);
            return Optional.ofNullable(cart);
        } catch (RuntimeException e) {
            log.error("[RedisCartRepositoryAdapter] falha ao ler cartId={} msg={}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void save(Cart cart, long ttlSeconds) {
        if (cart == null || norm(cart.getCartId()) == null || norm(cart.getCartId()).isBlank()) {
            log.error("[RedisCartRepositoryAdapter] save com cart/cartId inválido");
            throw new IllegalArgumentException("cart and cart.cartId are required");
        }

        final String id = norm(cart.getCartId());
        final String k = key(id);

        long ttl = ttlSeconds > 0 ? ttlSeconds : props.getTtl().getSeconds();
        if (ttl <= 0) {
            ttl = 24 * 60 * 60;
        }

        try {
            ValueOperations<String, Cart> ops = redis.opsForValue();
            ops.set(k, cart, Duration.ofSeconds(ttl));
            log.debug("[RedisCartRepositoryAdapter] SET cartId={} ttlSeconds={}", id, ttl);
        } catch (RuntimeException e) {
            log.error("[RedisCartRepositoryAdapter] falha ao salvar cartId={} msg={}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(String cartId) {
        final String id = norm(cartId);
        if (id == null || id.isBlank()) {
            log.warn("[RedisCartRepositoryAdapter] delete chamado com cartId vazio");
            return;
        }

        try {
            boolean removed = Boolean.TRUE.equals(redis.delete(key(id)));
            log.debug("[RedisCartRepositoryAdapter] DEL cartId={} removed={}", id, removed);
        } catch (RuntimeException e) {
            log.error("[RedisCartRepositoryAdapter] falha ao deletar cartId={} msg={}", id, e.getMessage(), e);
            throw e;
        }
    }
}
