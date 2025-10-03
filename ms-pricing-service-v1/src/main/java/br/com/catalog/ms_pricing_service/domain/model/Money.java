package br.com.catalog.ms_pricing_service.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

// value objects dinheiro mantido internamente me centavos para precisão
public record Money(long amountCents, Currency currency) {

    public static Money of(BigDecimal amount, Currency c) {
        long cents = amount.setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2).longValueExact();

        return new Money(cents, c);
    }
    public BigDecimal toBigDecimal() {

        return BigDecimal.valueOf(amountCents).movePointLeft(2);
    }
}
