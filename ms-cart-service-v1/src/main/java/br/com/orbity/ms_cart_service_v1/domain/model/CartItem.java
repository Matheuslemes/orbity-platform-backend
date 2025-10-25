package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    private String sku;

    private int quantity;

    private PriceSnapshot price;

}