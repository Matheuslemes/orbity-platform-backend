package br.com.orbity.ms_cart_service_v1.mapping;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.dto.CartDto;
import br.com.orbity.ms_cart_service_v1.dto.CartItemDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartDtoMapper {

    public CartDto toDto(Cart cart) {
        var items = cart.getItems().values().stream()
                .map(i -> new CartItemDto(
                        i.getSku(),
                        i.getQuantity(),
                        i.getPrice() == null ? null : i.getPrice().getUnitPrice(),
                        i.getPrice() == null ? null : i.getPrice().getCurrency()
                )).collect(Collectors.toList());
        return new CartDto(cart.getCartId(), items, cart.total());
    }

}
