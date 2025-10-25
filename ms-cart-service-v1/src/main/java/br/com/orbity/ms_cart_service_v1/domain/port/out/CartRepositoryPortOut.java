package br.com.orbity.ms_cart_service_v1.domain.port.out;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;

import java.util.Optional;

public interface CartRepositoryPortOut {

    Optional<Cart> findById(String cartId);

    void save(Cart cart, long ttlSeconds);

    void delete(String cartId);

}
