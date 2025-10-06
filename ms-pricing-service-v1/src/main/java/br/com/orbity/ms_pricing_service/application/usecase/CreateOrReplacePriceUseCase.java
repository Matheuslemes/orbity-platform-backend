package br.com.orbity.ms_pricing_service.application.usecase;


import br.com.orbity.ms_pricing_service.application.policy.TransactionalPolicy;
import br.com.orbity.ms_pricing_service.domain.event.PriceChangedEvent;
import br.com.orbity.ms_pricing_service.domain.model.Money;
import br.com.orbity.ms_pricing_service.domain.model.Price;
import br.com.orbity.ms_pricing_service.domain.port.in.CreateOrReplacePriceCommand;
import br.com.orbity.ms_pricing_service.domain.port.out.OutboxPortOut;
import br.com.orbity.ms_pricing_service.domain.port.out.PriceRepositoryPortOut;
import br.com.orbity.ms_pricing_service.domain.service.PricePolicyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Slf4j
@Service
public class CreateOrReplacePriceUseCase {

    private final PriceRepositoryPortOut repository;
    private final PricePolicyService policy;
    private final OutboxPortOut outbox;
    private final ObjectMapper om;

    public CreateOrReplacePriceUseCase(PriceRepositoryPortOut priceRepo, PricePolicyService policy,
                                       OutboxPortOut outbox, ObjectMapper om) {
        this.repository = priceRepo; this.policy = policy; this.outbox = outbox; this.om = om;
    }

    @TransactionalPolicy
    public UUID handle(CreateOrReplacePriceCommand cmd) {

        log.info("[CreateOrReplacePriceUseCase] - [handle] IN -> {}", safeCmd(cmd));

        if (cmd == null) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] Command is null");
            throw new IllegalArgumentException("Command is null");
        }

        if (isBlank(cmd.sku())) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] SKU is blank");
            throw new IllegalArgumentException("SKU obrigatório");
        }

        if (isBlank(cmd.currency())) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] Currency is blank sku={}", cmd.sku());
            throw new IllegalArgumentException("Moeda obrigatória");
        }

        if (cmd.amount() == null) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] Amount is null sku={}",cmd.sku());
            throw new IllegalArgumentException("Valor obrigatório");
        }

        if (cmd.amount().signum() < 0) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] - Amount negative sku={} amount={}", cmd.sku(), cmd.amount());
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }

        final Currency c;

        try {
            c = Currency.getInstance(cmd.currency());
        } catch (Exception e) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] Invalid currency sku={} currency={}", cmd.sku(), cmd.currency());
            throw new IllegalArgumentException("Moeda inválida: " + cmd.currency());
        }

        // nromalização e carga do agregado
        Money incoming = Money.of(cmd.amount(), c);
        Money normalized = policy.normalize(incoming);
        log.info("[CreateOrReplacePriceUseCase] - [handle] normalized amountCents={} currency={}",
                normalized.amountCents(), normalized.currency().getCurrencyCode());

        Price agg = repository.findBySku(cmd.sku())
                .orElseGet(() -> {
                    Price p = new Price(UUID.randomUUID(), cmd.sku(), c);
                    log.info("[CreateOrReplacePriceUseCase] - [handle] creating new aggregate id={} sku={}", p.id(), p.sku());
                    return p;
                });

        var oldActive = agg.activeVersion().orElse(null);
        var created = agg.replaceActive(normalized, Instant.now());
        log.info("[CreateOrReplacePriceUseCase] - [handle] replace active version priceId={} newVersionId={}",
                agg.id(), created.id());

        // persiste
        repository.save(agg);
        log.info("[CreateOrReplacePriceUseCase] - [handle] persisted aggregate id={} sku={}", agg.id(), agg.sku());

        // outbox
        PriceChangedEvent evt = new PriceChangedEvent(
                agg.sku(),
                normalized.currency().getCurrencyCode(),
                normalized.amountCents(),
                Instant.now(),
                cmd.reason()
        );

        try {
            String payload = om.writeValueAsString(evt);
            outbox.append("Price", agg.id().toString(), "PriceChangedEvent", payload);
            log.info("[CreateOrReplacePriceUseCase] - [handle] appended outbox aggregateId={} event=PriceChangeEvent", agg.id());
        } catch (JsonProcessingException e) {
            log.error("[CreateOrReplacePriceUseCase] - [handle] FAIL serialize outbox aggregateId={} error={}", agg.id(), e.getMessage(), e);
            throw new IllegalArgumentException("Errpr serializando evento", e);
        }

        // alerta de mudança suspeita
        boolean suspicious = policy.isSuspiciousChange(oldActive == null ? null : oldActive.money(), normalized);
        if (suspicious) {
            log.warn("[CreateOrReplacePriceUseCase] - [handle] suspecious change detected sku={} old={} new ={} reason={}",
                    agg.sku(),
                    oldActive == null ? null : oldActive.money(),
                    normalized,
                    cmd.reason());
        }

        log.info("[CreateOrReplacePriceUseCase] - [handle] OUT <- newVersionId={} sku={}", created.id(), agg.sku());

        return created.id();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // evita logar tudo em claro
    private static String safeCmd(CreateOrReplacePriceCommand cmd) {

        if (cmd == null) return "null";
        return "{sky=}" + cmd.sku() + ", currency=" + cmd.currency() + ", amount=" + cmd.amount() + ", reason=" + cmd.reason() + "}";
    }
}
