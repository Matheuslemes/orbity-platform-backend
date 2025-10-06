package br.com.orbity.ms_pricing_service.mapping;

import br.com.orbity.ms_pricing_service.domain.model.Price;
import br.com.orbity.ms_pricing_service.dto.PriceDto;
import org.springframework.stereotype.Component;

@Component
public class PriceDtoMapper {

    public PriceDto toDto(Price price) {
        var active = price.activeVersion().orElseThrow();
        var amount = active.money().toBigDecimal();

        return new PriceDto(
                price.sku(),
                active.money().currency().getCurrencyCode(),
                amount,
                true
        );
    }
}
