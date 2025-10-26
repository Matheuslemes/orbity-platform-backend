package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.config.CartProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.RemoveItemCommand;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveItemUseCase {

    private static final String ACTION = "REMOVE_ITEM";

    private final CartRepositoryPortOut cartRepo;
    private final CartEventPublisherPortOut publisher; // pode ser null
    private final CartProperties props;

    public void handle(@NonNull RemoveItemCommand cmd) {
        final String cartId = cmd.cartId();
        final String sku = cmd.sku();

        log.info("[RemoveItemUseCase] - [handle] IN -> cartId={} sku={}", safe(cartId), safe(sku));

        if (cartId == null || cartId.isBlank()) {
            log.error("[RemoveItemUseCase] - [handle] cartId é obrigatório");
            throw new IllegalArgumentException("cartId is required");
        }
        if (sku == null || sku.isBlank()) {
            log.error("[RemoveItemUseCase] - [handle] sku é obrigatório");
            throw new IllegalArgumentException("sku is required");
        }

        try {
            Cart cart = cartRepo.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

            boolean existed = cart.getItems().containsKey(sku);
            if (!existed) {
                log.warn("[RemoveItemUseCase] - [handle] sku não encontrado -> no-op cartId={} sku={}", cartId, sku);
            } else {
                cart.removeItem(sku);
                log.debug("[RemoveItemUseCase] - [handle] sku removido cartId={} sku={} itemsNow={}",
                        cartId, sku, cart.itemsSize());
            }

            long ttlSeconds = props.getTtl().getSeconds();
            cartRepo.save(cart, ttlSeconds);

            if (existed && publisher != null) {
                publisher.publishUpdated(cart.getCartId(), cart, ACTION);
                log.debug("[RemoveItemUseCase] - [handle] evento publicado action={} cartId={} itemsNow={}",
                        ACTION, cart.getCartId(), cart.itemsSize());
            }

            log.info("[RemoveItemUseCase] - [handle] OK -> cartId={} items={} ttlSeconds={}",
                    cart.getCartId(), cart.itemsSize(), ttlSeconds);

        } catch (RuntimeException re) {
            log.error("[RemoveItemUseCase] - [handle] runtime failure cartId={} sku={} msg={}",
                    safe(cartId), safe(sku), re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[RemoveItemUseCase] - [handle] failure cartId={} sku={} msg={}",
                    safe(cartId), safe(sku), e.getMessage(), e);
            throw new IllegalStateException("remove item failed", e);
        }
    }

    private String safe(String s) { return s == null ? "null" : s; }
}
