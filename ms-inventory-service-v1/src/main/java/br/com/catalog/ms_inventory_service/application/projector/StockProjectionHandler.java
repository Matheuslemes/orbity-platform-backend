package br.com.catalog.ms_inventory_service.application.projector;

import br.com.catalog.ms_inventory_service.domain.event.StockAdjusted;
import br.com.catalog.ms_inventory_service.domain.event.StockDecremented;
import br.com.catalog.ms_inventory_service.domain.event.StockReleased;
import br.com.catalog.ms_inventory_service.domain.event.StockReserved;
import br.com.catalog.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import org.springframework.stereotype.Component;

@Component
public class StockProjectionHandler {

    private final StockReadRepositoryPortOut readRepository;

    public StockProjectionHandler(StockReadRepositoryPortOut readRepository) {
        this.readRepository = readRepository;
    }

    public void on(Object ev) {
        if (ev instanceof StockAdjusted e) {

            var sku = normSku(e.sku());
            var current = currentOrZero(sku);
            var nextAvailable = clampNonNegative(e.newAvailableQty());
            var nextReserved  = clampNonNegative(current.reservedQty());
            readRepository.upsert(new StockReadRepositoryPortOut.StockRead(sku, nextAvailable, nextReserved));

        } else if (ev instanceof StockDecremented e) {

            var sku = normSku(e.sku());
            var current = currentOrZero(sku);
            var nextAvailable = clampNonNegative(current.availableQty() - e.delta());
            var nextReserved  = clampNonNegative(current.reservedQty());
            readRepository.upsert(new StockReadRepositoryPortOut.StockRead(sku, nextAvailable, nextReserved));

        } else if (ev instanceof StockReserved e) {

            var sku = normSku(e.sku());
            var current = currentOrZero(sku);
            var nextAvailable = clampNonNegative(current.availableQty() - e.quantity());
            var nextReserved  = clampNonNegative(current.reservedQty() + e.quantity());
            readRepository.upsert(new StockReadRepositoryPortOut.StockRead(sku, nextAvailable, nextReserved));

        } else if (ev instanceof StockReleased e) {

            var sku = normSku(e.sku());
            var current = currentOrZero(sku);
            var nextAvailable = clampNonNegative(current.availableQty() + e.quantity());
            var nextReserved = clampNonNegative(current.reservedQty() - e.quantity());
            readRepository.upsert(new StockReadRepositoryPortOut.StockRead(sku, nextAvailable, nextReserved));

        }
    }

    //helpers
    private StockReadRepositoryPortOut.StockRead currentOrZero(String sku) {

        return readRepository.findBySku(sku)
                .orElse(new StockReadRepositoryPortOut.StockRead(sku, 0L, 0L));

    }

    private static String normSku(String sku) {

        if (sku == null) throw new IllegalArgumentException("sku is required");
        var s = sku.trim();
        if (s.isEmpty()) throw new IllegalArgumentException("sku is blank");

        return s;

    }

    private static long clampNonNegative(long v) {

        return v < 0 ? 0 : v;

    }
}
