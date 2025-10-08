package br.com.orbity.customer.application.usecase;

import br.com.orbity.customer.application.policy.TransactionalPolicy;
import br.com.orbity.customer.domain.model.Consent;
import br.com.orbity.customer.domain.port.in.UpdateConsentCommand;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateConsentUseCase implements UpdateConsentCommand {

    private final CustomerRepositoryPortOut repository;
    private final TransactionalPolicy tx;

    @Override
    public void update(UUID customerId, boolean marketingOption, boolean termsAccepted, boolean dataProcessing) {

        log.info("[UpdateConsentUseCase] - [update] IN -> id={}", customerId);
        tx.runInTx(() -> {
            var c = new Consent(marketingOption, termsAccepted, dataProcessing, OffsetDateTime.now());
            repository.updateConsent(customerId, c);

        });

    }
}
