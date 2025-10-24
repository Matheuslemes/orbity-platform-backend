package br.com.orbity.ms_checkout_service_v1.application.usecase;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.in.GetCheckoutStatusQuery;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.SagaStateRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCheckoutStatusUseCase implements GetCheckoutStatusQuery {

    private final SagaStateRepositoryPortOut sagaRepo;

    @Override
    public Optional<Checkout> byId(UUID checkoutId) {

        log.info("[GetCheckoutStatusUseCase] - [byId] IN -> checkoutId={}", checkoutId);
        if (checkoutId == null) {

            log.error("[GetCheckoutStatusUseCase] - [byId] invalid argument: checkoutId is null");
            throw new IllegalArgumentException("checkoutId cannot be null");

        }

        var result = sagaRepo.byId(checkoutId);

        if (result.isPresent()) {

            var c = result.get();
            log.info("[GetCheckoutStatusUseCase] - [byId] found id={} status={} total={}",
                    c.id(), c.status(), c.totalAmount());

        } else {

            log.warn("[GetCheckoutStatusUseCase] - [byId] not found id={}", checkoutId);

        }

        return result;
    }

}
