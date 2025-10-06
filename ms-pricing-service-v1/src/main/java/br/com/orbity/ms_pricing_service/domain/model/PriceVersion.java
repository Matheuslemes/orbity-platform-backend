package br.com.orbity.ms_pricing_service.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// uma versão de preços com vigência (valid_from/valid_to) e flag de ativo
public class PriceVersion {

    private final UUID id;

    private final Money money;

    private final Instant validFrom;

    private Instant validTo; // null = aberta

    private boolean active;


    public PriceVersion(UUID id, Money money, Instant validFrom, Instant validTo, boolean active) {
        this.id = id; this.money = money; this.validFrom = validFrom; this.validTo = validTo; this.active = active;
    }

    public UUID id() { return id; }

    public Money money() { return money; }

    public Instant validFrom() { return validFrom; }

    public Instant validTo() { return validTo; }

    public boolean isActive() { return active; }

    public boolean isEffectiveAt(Instant t) {
        return !validFrom.isAfter(t) && (validTo == null || !validTo.isBefore(t));
    }

    public void closeAt(Instant t) {
        this.validTo = t;
        this.active = false;
    }

    @Override
    public boolean equals(Object o){

        return o instanceof PriceVersion pv && Objects.equals(id, pv.id);
    }

    @Override public int hashCode(){

        return Objects.hash(id);
    }
}
