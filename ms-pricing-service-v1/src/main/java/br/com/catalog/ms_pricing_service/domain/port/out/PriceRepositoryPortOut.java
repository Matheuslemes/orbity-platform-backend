package br.com.catalog.ms_pricing_service.domain.port.out;

import br.com.catalog.ms_pricing_service.domain.model.Price;

import java.util.Optional;

public interface PriceRepositoryPortOut {

    Optional<Price> findBySku(String sku);

    Price save(Price price); // persiste agregado + versões
}
