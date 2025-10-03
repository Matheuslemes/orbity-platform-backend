package br.com.catalog.ms_pricing_service.domain.model;

import java.time.Instant;
import java.util.*;

// aggregate rood price por sku. mantém lista de versões e ativa apenas 1
public class Price {

    private final UUID id;

    private final String sku;

    private final Currency currency; // ISO-4217, ex: BRL, USD

    private final List<PriceVersion> versions = new ArrayList<>();


    public Price(UUID id, String sku, Currency currency) {
        this.id = id;
        this.sku = sku;
        this.currency = currency;
    }


    public UUID id() { return id; }

    public String sku() { return sku; }

    public Currency currency() { return currency; }

    public List<PriceVersion> versions() { return Collections.unmodifiableList(versions); }

    public Optional<PriceVersion> activeVersion() {
        return versions.stream().filter(PriceVersion::isActive).findFirst();
    }

    // troca o preço ativo criando nova versão e encerrando a anterior
    public PriceVersion replaceActive(Money newMoney, Instant now) {

        activeVersion().ifPresent(v -> v.closeAt(now));
        PriceVersion created = new PriceVersion(UUID.randomUUID(), newMoney, now, null, true);
        versions.add(created);

        return created;

    }

    public void addVersion(PriceVersion v) { versions.add(v); } // para rehidratação
}
