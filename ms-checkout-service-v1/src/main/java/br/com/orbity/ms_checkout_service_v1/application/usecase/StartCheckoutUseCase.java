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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StartCheckoutUseCase implements StartCheckoutCommand {

    private static final String EVENT_TYPE = "checkout.started";

    private final PricingServicePortOut pricing;
    private final OutboxPortOut outbox;
    private final TransactionalPolicy tx;
    private final IdempotencyPolicy idem;
    private final ObjectMapper om;

    // opcionais (dependem do perfil escolhido)
    private final Optional<SagaStateRepositoryPortOut> sagaRepoOpt;
    private final Optional<CorrelationStorePortOut> correlationOpt;

    // TTL do lock de idempotência (segundos), configurável
    @Value("${app.checkout.idem-lock-ttl-seconds:60}")
    private int idemLockTtlSeconds;

    @Override
    public Checkout start(@NonNull Checkout draft, String idempotencyKey) {

        // Gera chave de idempotência (ex.: "checkout:start:{hash}")
        final String key = idem.key("checkout:start", idempotencyKey);

        // Tenta aplicar lock SE existir CorrelationStore (Redis). Caso contrário, segue sem lock.
        boolean locked = correlationOpt
                .map(c -> c.tryLock(key, idemLockTtlSeconds))
                .orElse(true);

        if (!locked) {
            throw new IllegalStateException("Duplicate or in-flight request");
        }

        try {
            return tx.inTx(() -> {

                // 1) Recalcula o total (defensivo) e aplica no draft
                var recomputed = pricing.recomputeTotal(draft);
                draft.setTotalAmount(recomputed);

                // 2) Marca status STARTED e timestamps se faltarem
                draft.setStatus(CheckoutStatus.STARTED);
                if (draft.createdAt() == null) draft.setCreatedAt(OffsetDateTime.now());
                draft.setUpdatedAt(OffsetDateTime.now());

                // 3) Persiste estado da SAGA se houver repositório (modo Postgres)
                sagaRepoOpt.ifPresent(repo -> repo.upsert(draft));

                // 4) Outbox → "checkout.started"
                var evt = new CheckoutStartedEvent(draft.id(), draft.customerId(), OffsetDateTime.now());
                String jsonPayload = toJson(evt);

                // agreggateId = checkoutId
                outbox.append(EVENT_TYPE, jsonPayload, draft.id());

                return draft;
            });

        } catch (RuntimeException re) {
            // repropaga Runtime
            throw re;

        } catch (Exception e) {
            // checked → IllegalState
            throw new IllegalStateException("start checkout failed", e);

        } finally {
            // Libera lock se houver CorrelationStore
            correlationOpt.ifPresent(c -> c.release(key));
        }
    }

    // -------- helpers --------

    private String toJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("serialize event failed", e);
        }
    }
}
