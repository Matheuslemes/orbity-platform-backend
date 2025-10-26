package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.config.CartProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.MergeCartCommand;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeCartUseCase {

    private static final String ACTION = "MERGE";

    private final CartRepositoryPortOut cartRepo;
    private final CartEventPublisherPortOut publisher;
    private final CartProperties props;

    public Cart handle(@NonNull MergeCartCommand cmd) {
        final String anonId = trimOrNull(cmd.anonymousCartId());
        final String userId = trimOrNull(cmd.userCartId());

        log.info("[MergeCartUseCase] - [handle] IN -> anonId={} userId={}", anonId, userId);

        if (anonId == null || anonId.isBlank()) {
            log.error("[MergeCartUseCase] - [handle] anonymousCartId é obrigatório");
            throw new IllegalArgumentException("anonymousCartId is required");
        }
        if (userId == null || userId.isBlank()) {
            log.error("[MergeCartUseCase] - [handle] userCartId é obrigatório");
            throw new IllegalArgumentException("userCartId is required");
        }

        if (anonId.equals(userId)) {
            Cart same = cartRepo.findById(userId).orElseGet(() -> Cart.builder().cartId(userId).build());
            log.warn("[MergeCartUseCase] - [handle] anonId == userId -> no-op (items={})", same.itemsSize());
            return same;
        }

        try {
            Cart anon = cartRepo.findById(anonId).orElseGet(() -> Cart.builder().cartId(anonId).build());
            Cart user = cartRepo.findById(userId).orElseGet(() -> Cart.builder().cartId(userId).build());

            if (anon.isEmpty()) {
                log.info("[MergeCartUseCase] - [handle] anon cart empty -> no-op; returning user cart (items={})",
                        user.itemsSize());
                return user;
            }

            user.mergeFrom(anon);

            long ttlSeconds = props.getTtl().getSeconds();
            cartRepo.save(user, ttlSeconds);
            cartRepo.delete(anon.getCartId());

            if (publisher != null) {
                publisher.publishMerged(anon.getCartId(), user.getCartId(), user);
                log.debug("[MergeCartUseCase] - [handle] evento publicado action={} anonId={} userId={} items={}",
                        ACTION, anon.getCartId(), user.getCartId(), user.itemsSize());
            }

            log.info("[MergeCartUseCase] - [handle] OK -> merged anonId={} → userId={} items={} ttlSeconds={}",
                    anonId, userId, user.itemsSize(), ttlSeconds);

            return user;

        } catch (RuntimeException re) {
            log.error("[MergeCartUseCase] - [handle] runtime failure anonId={} userId={} msg={}",
                    anonId, userId, re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[MergeCartUseCase] - [handle] failure anonId={} userId={} msg={}",
                    anonId, userId, e.getMessage(), e);
            throw new IllegalStateException("merge carts failed", e);
        }
    }

    private String trimOrNull(String s) { return s == null ? null : s.trim(); }

}
