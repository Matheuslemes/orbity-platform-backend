package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cart {

    private String cartId;

    @Builder.Default
    private Map<String, CartItem> items = new LinkedHashMap<>();

    @Builder.Default
    private String currency = "BRL";

    @Builder.Default
    private long createdAtEpochMs = System.currentTimeMillis();

    @Builder.Default
    private long updatedAtEpochMs = System.currentTimeMillis();


    public int itemsSize() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addOrIncrement(@NonNull CartItem item) {
        String sku = requireSku(item.getSku());
        int addQty = Math.max(item.getQuantity(), 0);

        items.merge(sku, item, (oldI, newI) -> {
            int newQty = Math.max(oldI.getQuantity(), 0) + addQty;
            oldI.setQuantity(newQty);

            if (newI.getPrice() != null) {
                if (oldI.getPrice() == null) {
                    oldI.setPrice(newI.getPrice());
                } else {
                    long oldCap = Optional.ofNullable(oldI.getPrice().getCapturedAtEpochMs()).orElse(0L);
                    long newCap = Optional.ofNullable(newI.getPrice().getCapturedAtEpochMs()).orElse(0L);
                    if (newCap >= oldCap) {
                        oldI.setPrice(newI.getPrice());
                    }
                }
            }
            return oldI;
        });

        touch();
    }

    public void setItemQuantity(@NonNull String sku, int quantity) {
        sku = requireSku(sku);
        if (quantity <= 0) {
            items.remove(sku);
        } else {
            CartItem i = items.get(sku);
            if (i == null) {
                items.put(sku, CartItem.builder().sku(sku).quantity(quantity).build());
            } else {
                i.setQuantity(quantity);
            }
        }
        touch();
    }

    public void removeItem(@NonNull String sku) {
        items.remove(requireSku(sku));
        touch();
    }

    public void mergeFrom(@NonNull Cart other) {
        if (other.items == null || other.items.isEmpty()) return;
        for (CartItem it : other.items.values()) {
            if (it == null || it.getSku() == null) continue;
            this.addOrIncrement(it);
        }
        if (this.currency == null || this.currency.isBlank()) {
            this.currency = other.currency;
        }
        touch();
    }


    private void touch() {
        this.updatedAtEpochMs = System.currentTimeMillis();
    }

    private String requireSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku is required");
        }
        return sku;
    }

    // Total com 2 casas decimais (HALF_UP)
    public BigDecimal total() {
        return total(2, RoundingMode.HALF_UP);
    }

    // Total com precisão customizada
    public BigDecimal total(int scale, RoundingMode rounding) {
        BigDecimal acc = BigDecimal.ZERO;

        for (CartItem i : items.values()) {
            BigDecimal unit = (i.getPrice() == null || i.getPrice().getUnitPrice() == null)
                    ? BigDecimal.ZERO
                    : i.getPrice().getUnitPrice();

            if (unit.signum() < 0) unit = BigDecimal.ZERO; // evita negativos indevidos

            int qty = Math.max(i.getQuantity(), 0);
            acc = acc.add(unit.multiply(BigDecimal.valueOf(qty)));
        }

        return acc.setScale(scale, rounding);
    }

}
