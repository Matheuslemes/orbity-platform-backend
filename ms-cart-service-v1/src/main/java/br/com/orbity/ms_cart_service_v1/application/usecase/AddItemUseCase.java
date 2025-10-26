package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.application.policy.IdempotencyPolicy;
import br.com.orbity.ms_cart_service_v1.config.CartProperties;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.model.CartItem;
import br.com.orbity.ms_cart_service_v1.domain.model.PriceSnapshot;
import br.com.orbity.ms_cart_service_v1.domain.port.in.AddItemCommand;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.InventoryLookupPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.PricingLookupPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddItemUseCase {

    private static final String EVENT_TYPE = "cart.updated";
    private static final String ACTION     = "ADD_ITEM";

    private final CartRepositoryPortOut cartRepo;
    private final PricingLookupPortOut pricing;
    private final InventoryLookupPortOut inventory;
    private final CartEventPublisherPortOut publisher;
    private final IdempotencyPolicy idem;
    private final CartProperties props;

    public Cart handle(@NonNull AddItemCommand cmd) {
        final String idemKey = idem.key(
                "cart:addItem",
                safe(cmd.cartId()) + ":" + safe(cmd.sku()) + ":" + cmd.quantity()
        );

        log.info("[AddItemUseCase] - [handle] IN -> cartId={} sku={} qty={} idemKey={}",
                safe(cmd.cartId()), safe(cmd.sku()), cmd.quantity(), idemKey);

        if (cmd.cartId() == null) throw new IllegalArgumentException("cartId is required");
        if (cmd.sku() == null || cmd.sku().isBlank()) throw new IllegalArgumentException("sku is required");
        if (cmd.quantity() <= 0) throw new IllegalArgumentException("quantity must be > 0");

        try {

            Cart cart = cartRepo.findById(cmd.cartId())
                    .orElseGet(() -> Cart.builder().cartId(cmd.cartId()).build());

            BigDecimal unitPrice = BigDecimal.ZERO;
            String currency = "BRL";

            if (pricing != null) {
                unitPrice = pricing.unitPrice(cmd.sku()).orElse(BigDecimal.ZERO);
                currency  = pricing.currency();
            }

            boolean available = (inventory == null) || inventory.isAvailable(cmd.sku(), cmd.quantity());
            if (!available) {
                log.warn("[AddItemUseCase] - [handle] indisponível sku={} qty={}", cmd.sku(), cmd.quantity());
                throw new IllegalStateException("SKU indisponível para quantidade solicitada");
            }

            var snap = PriceSnapshot.now(cmd.sku(), unitPrice, currency).normalize();

            cart.addOrIncrement(
                    CartItem.builder()
                            .sku(cmd.sku())
                            .quantity(cmd.quantity())
                            .price(snap)
                            .build()
                            .validate()
            );

            long ttlSeconds = props.getTtl().getSeconds();
            cartRepo.save(cart, ttlSeconds);

            if (publisher != null) {
                publisher.publishUpdated(cart.getCartId(), cart, ACTION);
                log.debug("[AddItemUseCase] - [handle] evento publicado type={} action={} cartId={}",
                        EVENT_TYPE, ACTION, cart.getCartId());
            }

            log.info("[AddItemUseCase] - [handle] OK -> cartId={} items={} ttlSeconds={}",
                    cart.getCartId(), cart.itemsSize(), ttlSeconds);

            return cart;

        } catch (RuntimeException re) {
            log.error("[AddItemUseCase] - [handle] runtime failure cartId={} msg={}",
                    safe(cmd.cartId()), re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[AddItemUseCase] - [handle] failure cartId={} msg={}",
                    safe(cmd.cartId()), e.getMessage(), e);
            throw new IllegalStateException("add item failed", e);
        }

    }

    private String safe(String s) { return s == null ? "null" : s; }

}
