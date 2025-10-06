package br.com.orbity.ms_inventory_service.domain.port.out;

import java.util.List;
import java.util.Optional;

public interface StockReadRepositoryPortOut {

    record StockRead(String sku, long availableQty, long reservedQty) {}

    Optional<StockRead> findBySku(String sku);

    void upsert(StockRead read);

    List<StockRead> findAll(int page, int size);

}