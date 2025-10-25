package br.com.orbity.ms_cart_service_v1.domain.port.out;

import java.math.BigDecimal;
import java.util.Optional;

public interface PricingLookupPortOut {

    Optional<BigDecimal> unitPrice(String sku);

    default String currency() {

        return "BRL";

    }

}
