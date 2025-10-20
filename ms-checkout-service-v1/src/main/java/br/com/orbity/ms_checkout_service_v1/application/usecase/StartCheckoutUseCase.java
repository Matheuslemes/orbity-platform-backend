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
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class StartCheckoutUseCase implements StartCheckoutCommand {

    private final SagaStateRepositoryPortOut sagaRepo;
    private final PricingServicePortOut pricing;
    private final CorrelationStorePortOut correlation;
    private final OutboxPortOut outbox;
    private final TransactionalPolicy tx;
    private final IdempotencyPolicy idem;
    private final ObjectMapper om;

    @Override
    public Checkout start(Checkout draft, String idempotencyKey) {

        String key = idem.key("checkout:start", idempotencyKey);
        if (!correlation.tryLock(key, 60)) {
            throw new IllegalStateException("Duplicate or in-flight request");
        }

        try {
            return tx.inTx(() -> {
                // recompute total with pricing (defensive)
                var total = pricing.recomputeTotal(draft);
                draft.setStatus(CheckoutStatus.STARTED);

                // persist SAGA state if Postgres profile
                sagaRepo.upsert(draft);

                // outbox: checkout.started
                var evt = new CheckoutStartedEvent(draft.id(), draft.customerId(), OffsetDateTime.now());
                outbox.append(evt.getClass().getSimpleName(), om.writeValueAsString(evt), draft.id());

                return draft;

            });

        } catch (RuntimeException re) {

            throw re;

        } catch (Exception e) {

            throw new IllegalStateException("start checkout failed", e);

        } finally {

            correlation.release(key);
        }

    }

}
