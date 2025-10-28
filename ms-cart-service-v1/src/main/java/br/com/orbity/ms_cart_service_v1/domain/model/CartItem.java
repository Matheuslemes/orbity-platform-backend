package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(of = "sku")
public class CartItem {


    private String sku;

    @Builder.Default
    private int quantity = 0;

    private PriceSnapshot price;

    public CartItem validate() {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku is required");
        }
        if (quantity < 0) {
            quantity = 0;
        }
        return this;
    }


}
