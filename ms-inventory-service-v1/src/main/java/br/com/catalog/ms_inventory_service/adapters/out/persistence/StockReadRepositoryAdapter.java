package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import br.com.catalog.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StockReadRepositoryAdapter implements StockReadRepositoryPortOut {

    private final StockReadSpringRepository repo;

    public StockReadRepositoryAdapter(StockReadSpringRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<StockRead> findBySku(String sku) {

        return repo.findById(sku)
                .map(e -> new StockRead(e.getSku(), nz(e.getAvailableQty()), nz(e.getReservedQty())));

    }

    @Override
    public void upsert(StockRead read) {

        var e = repo.findById(read.sku()).orElseGet(StockReadJpaEntity::new);
        e.setSku(read.sku());
        e.setAvailableQty(read.availableQty());
        e.setReservedQty(read.reservedQty());
        e.setUpdatedAt(OffsetDateTime.now());
        repo.save(e);

        log.debug("Upsert stock_read sku={} available={} reserved={}", read.sku(), read.availableQty(), read.reservedQty());

    }

    @Override
    public List<StockRead> findAll(int page, int size) {

        return repo.findAll().stream()
                .map(e -> new StockRead(e.getSku(), nz(e.getAvailableQty()), nz(e.getReservedQty())))
                .toList();

    }

    private static long nz(Long v) { return v == null ? 0L : v; }

}