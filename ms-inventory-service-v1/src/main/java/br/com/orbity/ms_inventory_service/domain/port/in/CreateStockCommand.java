package br.com.orbity.ms_inventory_service.domain.port.in;


import br.com.orbity.ms_inventory_service.domain.model.StockAggregate;

public interface CreateStockCommand {

    StockAggregate create(String sku, long initialQty);

}
