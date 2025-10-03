package br.com.catalog_plataform.ms_catalog_service_v1.domain.model;

import java.math.BigDecimal;

public record Price(String currency, BigDecimal amount) {

    public Price {

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency é obrigatório");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount é obrigatório");
        }

    }
}
