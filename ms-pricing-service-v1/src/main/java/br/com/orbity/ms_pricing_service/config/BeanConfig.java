package br.com.orbity.ms_pricing_service.config;

import br.com.orbity.ms_pricing_service.application.usecase.CreateOrReplacePriceUseCase;
import br.com.orbity.ms_pricing_service.domain.port.out.OutboxPortOut;
import br.com.orbity.ms_pricing_service.domain.port.out.PriceRepositoryPortOut;
import br.com.orbity.ms_pricing_service.domain.service.PricePolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PricePolicyService pricePolicyService() {

        return new PricePolicyService();
    }

    @Bean
    public CreateOrReplacePriceUseCase createOrReplacePriceUseCase(
            PriceRepositoryPortOut repository,
            PricePolicyService policy,
            OutboxPortOut outbox,
            ObjectMapper objectMapper
    ) {
        return new CreateOrReplacePriceUseCase(repository, policy, outbox, objectMapper);
    }
}
