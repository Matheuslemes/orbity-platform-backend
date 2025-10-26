package br.com.orbity.ms_cart_service_v1.adapters.out.http;

import br.com.orbity.ms_cart_service_v1.domain.port.out.InventoryLookupPortOut;

public class NoOpInventoryLookupAdapter implements InventoryLookupPortOut {

    @Override
    public boolean isAvailable(String sku, int qty) {
        return true;
    }

    @Override
    public Integer availableQuantity(String sku) {
        return Integer.MAX_VALUE;
    }
}