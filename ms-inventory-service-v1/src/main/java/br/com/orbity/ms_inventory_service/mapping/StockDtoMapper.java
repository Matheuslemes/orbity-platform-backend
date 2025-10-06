package br.com.orbity.ms_inventory_service.mapping;


import br.com.orbity.ms_inventory_service.domain.model.StockAggregate;

import br.com.orbity.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import br.com.orbity.ms_inventory_service.dto.StockDto;
import org.springframework.stereotype.Component;

@Component
public class StockDtoMapper {


    public StockDto toDto(StockReadRepositoryPortOut.StockRead read) {
        if (read == null) return null;
        final String sku = sanitizeSku(read.sku());
        final long available = nz(read.availableQty());
        final long reserved  = nz(read.reservedQty());
        return new StockDto(null, sku, available, reserved);
    }

    public StockDto toDto(StockAggregate agg) {
        if (agg == null) return null;
        return new StockDto(
                agg.getId(),
                sanitizeSku(agg.getSku()),
                agg.getAvailableQty(),
                agg.getReservedQty()
        );
    }

    private static String sanitizeSku(String sku) {
        return sku == null ? null : sku.trim();
    }

    private static long nz(Long v) { return v == null ? 0L : v; }
}