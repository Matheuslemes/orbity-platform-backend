package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.config.CartProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.UpdateItemCommand;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateItemUseCase {

    private static final String ACTION = "UPDATE_ITEM";

    private final CartRepositoryPortOut cartRepo;
    private final CartEventPublisherPortOut publisher;
    private final CartProperties props;

    public void handle(@NonNull UpdateItemCommand cmd) {
        final String cartId = cmd.cartId();
        final String sku    = cmd.sku();
        final int qty       = cmd.quantity();

        log.info("[UpdateItemUseCase] - [handle] IN -> cartId={} sku={} qty={}", safe(cartId), safe(sku), qty);

        if (cartId == null || cartId.isBlank()) {
            log.error("[UpdateItemUseCase] - [handle] cartId é obrigatório");
            throw new IllegalArgumentException("cartId is required");
        }
        if (sku == null || sku.isBlank()) {
            log.error("[UpdateItemUseCase] - [handle] sku é obrigatório");
            throw new IllegalArgumentException("sku is required");
        }

        try {
            Cart cart = cartRepo.findById(cartId)
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

            var item = cart.getItems().get(sku);
            if (item == null) {
                log.error("[UpdateItemUseCase] - [handle] SKU não está no carrinho cartId={} sku={}", cartId, sku);
                throw new IllegalArgumentException("SKU not in cart");
            }

            final int oldQty = item.getQuantity();

            if (qty <= 0) {
                cart.removeItem(sku);
                log.debug("[UpdateItemUseCase] - [handle] SKU removido cartId={} sku={} oldQty={}", cartId, sku, oldQty);

            } else if (qty == oldQty) {
                log.warn("[UpdateItemUseCase] - [handle] no-op (mesma quantidade) cartId={} sku={} qty={}", cartId, sku, qty);

            } else {
                cart.setItemQuantity(sku, qty);
                log.debug("[UpdateItemUseCase] - [handle] quantidade atualizada cartId={} sku={} oldQty={} newQty={}",
                        cartId, sku, oldQty, qty);
            }

            long ttlSeconds = props.getTtl().getSeconds();
            cartRepo.save(cart, ttlSeconds);

            if ((qty <= 0 || qty != oldQty) && publisher != null) {
                publisher.publishUpdated(cart.getCartId(), cart, ACTION);
                log.debug("[UpdateItemUseCase] - [handle] evento publicado action={} cartId={} itemsNow={}",
                        ACTION, cart.getCartId(), cart.itemsSize());
            }

            log.info("[UpdateItemUseCase] - [handle] OK -> cartId={} items={} ttlSeconds={}",
                    cart.getCartId(), cart.itemsSize(), ttlSeconds);

        } catch (RuntimeException re) {
            log.error("[UpdateItemUseCase] - [handle] runtime failure cartId={} sku={} msg={}",
                    safe(cartId), safe(sku), re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[UpdateItemUseCase] - [handle] failure cartId={} sku={} msg={}",
                    safe(cartId), safe(sku), e.getMessage(), e);
            throw new IllegalStateException("update item failed", e);
        }
    }

    private String safe(String s) { return (s == null || s.isBlank()) ? "null" : s; }

}
