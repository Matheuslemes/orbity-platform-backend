package br.com.orbity.ms_checkout_service_v1.application.usecase;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.in.GetCheckoutStatusQuery;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.SagaStateRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCheckoutStatusUseCase implements GetCheckoutStatusQuery {

    private final SagaStateRepositoryPortOut sagaRepo;

    @Override
    public Optional<Checkout> byId(UUID checkoutId) {
        return sagaRepo.byId(checkoutId);
    }
}
