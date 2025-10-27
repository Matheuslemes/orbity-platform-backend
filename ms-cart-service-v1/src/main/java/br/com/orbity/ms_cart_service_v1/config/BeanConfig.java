package br.com.orbity.ms_cart_service_v1.config;

import br.com.orbity.ms_cart_service_v1.adapters.out.http.NoOpCartEventPublisher;
import br.com.orbity.ms_cart_service_v1.adapters.out.http.NoOpInventoryLookupAdapter;
import br.com.orbity.ms_cart_service_v1.application.policy.IdempotencyPolicy;
import br.com.orbity.ms_cart_service_v1.domain.model.PriceSnapshot;
import br.com.orbity.ms_cart_service_v1.domain.port.out.CartEventPublisherPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.InventoryLookupPortOut;
import br.com.orbity.ms_cart_service_v1.domain.port.out.PricingLookupPortOut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean(PricingLookupPortOut.class)
    public PricingLookupPortOut pricingLookupPortOut() {
        return new PricingLookupPortOut() {
            @Override
            public Optional<BigDecimal> unitPrice(String sku) {
                return Optional.of(BigDecimal.ZERO);
            }

            @Override
            public PriceSnapshot getCurrentPrice(String sku) {
                return new PriceSnapshot(sku, BigDecimal.ZERO, "BRL", Instant.now());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(InventoryLookupPortOut.class)
    public InventoryLookupPortOut inventoryLookupPortOut() {
        return new NoOpInventoryLookupAdapter();
    }

    @Bean
    public CartEventPublisherPortOut cartEventPublisherPortOut() {
        return new NoOpCartEventPublisher();

    }

    @Bean
    public IdempotencyPolicy idempotencyPolicy() {
        return new IdempotencyPolicy();
    }
}