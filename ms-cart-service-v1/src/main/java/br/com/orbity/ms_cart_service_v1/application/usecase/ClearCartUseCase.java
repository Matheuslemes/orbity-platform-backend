package br.com.orbity.ms_cart_service_v1.application.usecase;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.ClearCartCommand;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClearCartUseCase {

    private static final String ACTION = "CLEAR_CART";

    private final CartRepositoryPortOut repo;
    private final CartEventPublisherPortOut publisher; // pode ser null

    @Autowired
    public ClearCartUseCase(
            CartRepositoryPortOut repo,
            @Nullable CartEventPublisherPortOut publisher
    ) {
        this.repo = repo;
        this.publisher = publisher;
    }

    /**
     * Limpa (remove) o carrinho da store.
     * Regras:
     * - Se o carrinho não existir, operação é idempotente (no-op com log).
     * - Após a remoção, publica evento (se publisher presente) com razão "CLEAR_CART".
     *   (mantendo compatibilidade: cart = null no evento)
     */
    public void handle(@NonNull ClearCartCommand cmd) {
        final String cartId = cmd.cartId();
        log.info("[ClearCartUseCase] - [handle] IN -> cartId={}", safe(cartId));

        if (cartId == null || cartId.isBlank()) {
            log.error("[ClearCartUseCase] - [handle] cartId é obrigatório");
            throw new IllegalArgumentException("cartId is required");
        }

        try {
            // tenta carregar só para enriquecer logs (idempotente se não existir)
            Cart existing = repo.findById(cartId).orElse(null);

            if (existing == null) {
                log.warn("[ClearCartUseCase] - [handle] cart inexistente -> no-op cartId={}", cartId);
            } else {
                log.debug("[ClearCartUseCase] - [handle] cart encontrado items={} total={}",
                        existing.itemsSize(), existing.total());
            }

            // remove da store (idempotente)
            repo.delete(cartId);

            // publica evento (mantendo a lógica original: cart=null)
            if (publisher != null) {
                publisher.publishUpdated(cartId, null, ACTION);
                log.debug("[ClearCartUseCase] - [handle] evento publicado action={} cartId={}", ACTION, cartId);
            }

            log.info("[ClearCartUseCase] - [handle] OK -> cartId={} cleared", cartId);

        } catch (RuntimeException re) {
            log.error("[ClearCartUseCase] - [handle] runtime failure cartId={} msg={}", safe(cartId), re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            log.error("[ClearCartUseCase] - [handle] failure cartId={} msg={}", safe(cartId), e.getMessage(), e);
            throw new IllegalStateException("clear cart failed", e);
        }
    }

    // helpers
    private String safe(String s) { return (s == null || s.isBlank()) ? "null" : s; }
}
