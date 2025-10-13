package br.com.catalog.ms_orders_service_v1.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(

        BigDecimal amount,
        String currency

) {

    public Money {
        Objects.requireNonNull(amount, "amount");

        Objects.requireNonNull(currency, "currency");

    }

    public static Money of(BigDecimal amt, String cur){

        return new Money(amt, cur);

    }
}