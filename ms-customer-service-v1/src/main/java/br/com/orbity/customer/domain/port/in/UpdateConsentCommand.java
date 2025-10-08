package br.com.orbity.customer.domain.port.in;

import java.util.UUID;

public interface UpdateConsentCommand {

    void update(UUID customerId, boolean marketingOption, boolean termsAccepted, boolean dataProcessing);

}
