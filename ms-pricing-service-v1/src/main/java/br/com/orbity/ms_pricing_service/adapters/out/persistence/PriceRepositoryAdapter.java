package br.com.orbity.ms_pricing_service.adapters.out.persistence;

import br.com.orbity.ms_pricing_service.domain.model.Price;
import br.com.orbity.ms_pricing_service.domain.port.out.PriceRepositoryPortOut;
import br.com.orbity.ms_pricing_service.mapping.PriceJpaMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PriceRepositoryAdapter implements PriceRepositoryPortOut {

    private final PriceSpringData repository;

    private final PriceJpaMapper mapper;

    public PriceRepositoryAdapter(PriceSpringData repository, PriceJpaMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;

    }

    @Override
    public Optional<Price> findBySku(String sku) {

        return repository.findBySku(sku).map(mapper::toDomain);

    }

    @Override
    public Price save(Price price) {

        var entity = mapper.toEntity(price);
        price.activeVersion().ifPresent(v -> {
            entity.setAmountCents(v.money().amountCents());
            entity.setCurrency(v.money().currency().getCurrencyCode());
        });

        var now = Instant.now();
        if (entity.getCreatedAt() == null) entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        entity.getVersions().forEach(v -> v.setPrice(entity));

        var saved = repository.save(entity);

        return mapper.toDomain(saved);

    }
}
