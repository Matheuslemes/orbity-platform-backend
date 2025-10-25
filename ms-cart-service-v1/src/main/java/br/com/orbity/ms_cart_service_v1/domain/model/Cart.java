package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    private String cartId;

    @Builder.Default
    private Map<String, CartItem> items = new LinkedHashMap<>();

    public BigDecimal total() {

        return items.values().stream()
                .map(i -> i.getPrice() == null || i.getPrice().getUnitPrice() == null
                        ? BigDecimal.ZERO
                        : i.getPrice().getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public void addOrIncrement(CartItem item) {

        items.merge(item.getSku(), item, (oldI, newI) -> {
            oldI.setQuantity(oldI.getQuantity() + newI.getQuantity());
            if (newI.getPrice() != null) oldI.setPrice(newI.getPrice());
            return oldI;
        });

    }

}