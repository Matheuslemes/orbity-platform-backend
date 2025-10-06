package br.com.orbity.ms_inventory_service.mapping;


import br.com.orbity.ms_inventory_service.adapters.out.persistence.StockReadJpaEntity;
import br.com.orbity.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import org.springframework.stereotype.Component;

@Component
public class StockReadJpaMapper {

    public StockReadRepositoryPortOut.StockRead toRead(StockReadJpaEntity e) {
        if (e == null) return null;
        return new StockReadRepositoryPortOut.StockRead(
                sanitizeSku(e.getSku()),
                nz(e.getAvailableQty()),
                nz(e.getReservedQty())
        );
    }

    public StockReadJpaEntity toEntity(StockReadRepositoryPortOut.StockRead r) {
        if (r == null) return null;
        var entity = new StockReadJpaEntity();
        entity.setSku(sanitizeSku(r.sku()));
        entity.setAvailableQty(nz(r.availableQty()));
        entity.setReservedQty(nz(r.reservedQty())); // << corrigido (antes: reservadQty)
        return entity;
    }

    private static String sanitizeSku(String sku) {
        return sku == null ? null : sku.trim();
    }

    private static long nz(Long v) { return v == null ? 0L : v; }
}