package br.com.orbity.ms_cart_service_v1.domain.port.out;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;

public interface CartEventPublisherPortOut {

    void publishUpdated(String cartId, Cart cart, String reason);

    void publishMerged(String from, String to, Cart merged);

    void publishCheckedOut(String cartId);

}
