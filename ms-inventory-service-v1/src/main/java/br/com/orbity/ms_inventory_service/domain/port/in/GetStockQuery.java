package br.com.orbity.ms_inventory_service.domain.port.in;


import br.com.orbity.ms_inventory_service.domain.model.StockAggregate;
import br.com.orbity.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetStockQuery {

    Optional<StockAggregate> byId(UUID aggregateId);

    Optional<StockReadRepositoryPortOut.StockRead> bySku(String sku);

    List<StockReadRepositoryPortOut.StockRead> list(int page, int size);

}
