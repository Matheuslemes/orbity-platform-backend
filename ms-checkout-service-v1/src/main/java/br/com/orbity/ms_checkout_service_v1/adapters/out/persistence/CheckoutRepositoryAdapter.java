package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.SagaStateRepositoryPortOut;
import br.com.orbity.ms_checkout_service_v1.mapping.CheckoutJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "catalog.profile", name = "mode", havingValue = "postgres-saga", matchIfMissing = false)
public class CheckoutRepositoryAdapter implements SagaStateRepositoryPortOut {

    private final CheckoutExecutionSpringData repo;
    private final CheckoutJpaMapper mapper;

    @Override
    public void upsert(Checkout checkout) {

        var e = mapper.toEntity(checkout);

        repo.save(e);

    }

    @Override
    public Optional<Checkout> byId(UUID checkoutId) {

        return repo.findById(checkoutId).map(mapper::toDomain);

    }
}
