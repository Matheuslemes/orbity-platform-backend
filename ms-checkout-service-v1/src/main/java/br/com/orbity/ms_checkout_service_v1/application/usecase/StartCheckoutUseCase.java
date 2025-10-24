package br.com.orbity.ms_checkout_service_v1.application.usecase;

import br.com.orbity.ms_checkout_service_v1.application.policy.IdempotencyPolicy;
import br.com.orbity.ms_checkout_service_v1.application.policy.TransactionalPolicy;
import br.com.orbity.ms_checkout_service_v1.domain.event.CheckoutStartedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutStatus;
import br.com.orbity.ms_checkout_service_v1.domain.port.in.StartCheckoutCommand;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.CorrelationStorePortOut;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.OutboxPortOut;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.PricingServicePortOut;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.SagaStateRepositoryPortOut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartCheckoutUseCase implements StartCheckoutCommand {

    private static final String EVENT_TYPE = "checkout.started";

    private final PricingServicePortOut pricing;
    private final OutboxPortOut outbox;
    private final TransactionalPolicy tx;
    private final IdempotencyPolicy idem;
    private final ObjectMapper om;

    private final Optional<SagaStateRepositoryPortOut> sagaRepoOpt;
    private final Optional<CorrelationStorePortOut> correlationOpt;

    @Value("${app.checkout.idem-lock-ttl-seconds:60}")
    private int idemLockTtlSeconds;

    @Override
    public Checkout start(@NonNull Checkout draft, String idempotencyKey) {
        log.info("[StartCheckoutUseCase] - [start] IN -> draftId={} customerId={} idemKeyPresent={}",
                safe(draft.id()), safe(draft.customerId()), idempotencyKey != null);

        if (draft.id() == null || draft.customerId() == null) {
            log.error("[StartCheckoutUseCase] - [start] invalid draft: id/customerId are required");
            throw new IllegalArgumentException("draft.id and draft.customerId are required");
        }

        final String idemKey = idem.key("checkout:start",
                idempotencyKey != null ? idempotencyKey : draft.id().toString());

        boolean locked = correlationOpt.map(c -> c.tryLock(idemKey, idemLockTtlSeconds)).orElse(true);

        if (!locked) {

            log.warn("[StartCheckoutUseCase] - [start] idempotency lock denied key={} ttlSeconds={}", idemKey, idemLockTtlSeconds);
            throw new IllegalStateException("Duplicate or in-flight request");

        }

        try {

            return tx.inTx(() -> {
                var recomputedTotal = pricing.recomputeTotal(draft);

                var now = OffsetDateTime.now();
                var started = new Checkout(
                        draft.id(),
                        draft.customerId(),
                        draft.items(),
                        draft.shippingAddress(),
                        draft.paymentInfo(),
                        recomputedTotal,
                        CheckoutStatus.STARTED,
                        draft.saga(),                               // mantemos o estado atual da SAGA (se houver)
                        draft.createdAt() != null ? draft.createdAt() : now,
                        now
                );

                sagaRepoOpt.ifPresent(repo -> {
                    log.debug("[StartCheckoutUseCase] - [start] persisting SAGA state id={}", started.id());
                    repo.upsert(started);
                });

                var evt = new CheckoutStartedEvent(started.id(), started.customerId(), now);

                outbox.append(EVENT_TYPE, toJson(evt), started.id());
                log.info("[StartCheckoutUseCase] - [start] appended outbox event type={} aggregateId={}",
                        EVENT_TYPE, started.id());

                log.info("[StartCheckoutUseCase] - [start] OK id={} status={} total={}",
                        started.id(), started.status(), started.totalAmount());

                return started;

            });

        } catch (RuntimeException re) {

            log.error("[StartCheckoutUseCase] - [start] runtime failure id={} msg={}",
                    draft.id(), re.getMessage(), re);
            throw re;

        } catch (Exception e) {

            log.error("[StartCheckoutUseCase] - [start] failure id={} msg={}", draft.id(), e.getMessage(), e);
            throw new IllegalStateException("start checkout failed", e);

        } finally {

            correlationOpt.ifPresent(c -> {
                c.release(idemKey);
                log.debug("[StartCheckoutUseCase] - [start] idempotency lock released key={}", idemKey);
            });

        }

    }

    private JsonNode toJson(Object obj) {

        return om.valueToTree(obj);

    }

    private String safe(UUID id) {

        return id == null ? "null" : id.toString();

    }

}
