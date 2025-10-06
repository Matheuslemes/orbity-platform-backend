package br.com.orbity.ms_pricing_service.domain.service;

// regras simpels: pisos, tetos, arredondamentos, impostos, etc

import br.com.orbity.ms_pricing_service.domain.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricePolicyService {

    public Money normalize(Money candidate) {
        // ex: piso de R$ 1.00 e teto de R$ 99999.99
        long cents = Math.max(100, Math.min(candidate.amountCents(), 9_999_999));

        return new Money(cents, candidate.currency());

    }

    public boolean isSuspiciousChange(Money oldPrice, Money newPrice) {

        if (oldPrice == null) return false;
        BigDecimal oldV = oldPrice.toBigDecimal();
        BigDecimal newV = newPrice.toBigDecimal();

        // ex: variação > 80% é suspeita
        BigDecimal delta = newV.subtract(oldV).abs();

        return oldV.compareTo(BigDecimal.ZERO) > 0 &&
                delta.divide(oldV, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal("0.8")) > 0;
    }
}
