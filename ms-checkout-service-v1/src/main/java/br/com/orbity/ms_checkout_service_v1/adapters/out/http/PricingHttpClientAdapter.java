package br.com.orbity.ms_checkout_service_v1.adapters.out.http;

import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.PricingServicePortOut;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PricingHttpClientAdapter implements PricingServicePortOut {

    @Override
    public BigDecimal recomputeTotal(Checkout checkout) {

        // TODO: chamar ms-pricing; por enquanto soma os itens
        return checkout.items().stream()
                .map(i -> i.lineTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

}
