package br.com.orbity.ms_cart_service_v1.domain.port.out;

public interface InventoryLookupPortOut {

    boolean isAvailable(String sku, int requiredQty);

    Integer availableQuantity(String sku);
}
