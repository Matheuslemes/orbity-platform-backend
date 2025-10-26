package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.GetCartQuery;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCartUseCase {

    private final CartRepositoryPortOut cartRepo;

    public Cart handle(@NonNull GetCartQuery q) {
        final String cartId = q.cartId();

        log.info("[GetCartUseCase] - [handle] IN -> cartId={}", safe(cartId));

        if (cartId == null || cartId.isBlank()) {
            log.error("[GetCartUseCase] - [handle] cartId é obrigatório");
            throw new IllegalArgumentException("cartId is required");
        }

        try {
            var cart = cartRepo.findById(cartId)
                    .orElseGet(() -> Cart.builder().cartId(cartId).build());

            log.info("[GetCartUseCase] - [handle] OK -> cartId={} items={}",
                    cart.getCartId(), cart.itemsSize());

            return cart;

        } catch (RuntimeException re) {
            log.error("[GetCartUseCase] - [handle] runtime failure cartId={} msg={}",
                    safe(cartId), re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[GetCartUseCase] - [handle] failure cartId={} msg={}",
                    safe(cartId), e.getMessage(), e);
            throw new IllegalStateException("get cart failed", e);
        }
    }

    private String safe(String s) { return s == null ? "null" : s; }

}
