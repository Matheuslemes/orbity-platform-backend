package br.com.orbity.ms_cart_service_v1.adapters.out.http;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpCartEventPublisher implements CartEventPublisherPortOut {

    private static final Logger log = LoggerFactory.getLogger(NoOpCartEventPublisher.class);

    @Override
    public void publishUpdated(String cartId, Cart cart, String reason) {

        if (log.isDebugEnabled()) {
            log.debug("[NoOp] cart.updated - cartId={}, items={}, reason={}",
                    cartId,
                    cart != null ? cart.getItems().size() : 0,
                    reason);
        } else {
            log.info("[NoOp] cart.updated - cartId={}", cartId);
        }
    }

    @Override
    public void publishMerged(String from, String to, Cart merged) {
        if (log.isDebugEnabled()) {
            log.debug("[NoOp] cart.merged - from={}, to={}, items={}",
                    from, to,
                    merged != null ? merged.getItems().size() : 0);
        } else {
            log.info("[NoOp] cart.merged - from={} -> to={}", from, to);
        }
    }

}
