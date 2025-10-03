package br.com.catalog.ms_pricing_service.mapping;

import br.com.catalog.ms_pricing_service.adapters.out.persistence.PriceJpaEntity;
import br.com.catalog.ms_pricing_service.adapters.out.persistence.PriceVersionJpaEntity;
import br.com.catalog.ms_pricing_service.domain.model.Money;
import br.com.catalog.ms_pricing_service.domain.model.Price;
import br.com.catalog.ms_pricing_service.domain.model.PriceVersion;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Currency;
import java.util.stream.Collectors;

@Component
public class PriceJpaMapper {

    // Entity -> Domain
    public Price toDomain(PriceJpaEntity e) {
        var p = new Price(
                e.getId(),
                e.getSku(),
                Currency.getInstance(e.getCurrency())
        );

        for (var v : e.getVersions()) {
            var money = new Money(v.getAmountCents(), Currency.getInstance(v.getCurrency()));
            var pv = new PriceVersion(v.getId(), money, v.getValidFrom(), v.getValidTo(), v.isActive());
            p.addVersion(pv);
        }
        return p;
    }

    // Domain -> Entity (full replace das versões)
    public PriceJpaEntity toEntity(Price agg) {
        var e = new PriceJpaEntity();

        // campos raiz
        e.setId(agg.id());
        e.setSku(agg.sku());
        e.setCurrency(agg.currency().getCurrencyCode());

        // timestamps: se for novo, define created; updated é gerenciado pelo @PreUpdate
        Instant now = Instant.now();
        if (e.getCreatedAt() == null) {
            e.setCreatedAt(now);
        }
        e.setUpdatedAt(now);

        // limpar e remontar versões (estratégia "replace")
        e.clearVersions();
        e.getVersions().addAll(
                agg.versions().stream().map(v -> {
                    var ev = new PriceVersionJpaEntity();
                    ev.setId(v.id());
                    ev.setAmountCents(v.money().amountCents());
                    ev.setCurrency(v.money().currency().getCurrencyCode());
                    ev.setValidFrom(v.validFrom());
                    ev.setValidTo(v.validTo());
                    ev.setActive(v.isActive());
                    ev.setPrice(e); // vínculo bidirecional
                    return ev;
                }).collect(Collectors.toList())
        );

        // espelho do valor ativo para consultas rápidas
        agg.activeVersion().ifPresent(active ->
                e.setAmountCents(active.money().amountCents())
        );

        return e;
    }
}
