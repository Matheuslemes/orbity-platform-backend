package br.com.orbity.ms_cart_service_v1.domain.event;

import br.com.orbity.ms_cart_service_v1.domain.model.Cart;

public record CartMergedEvent(

        String fromCartId,
        String toCartId,
        Cart merged

) { }

