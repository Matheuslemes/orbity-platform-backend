package br.com.orbity.ms_inventory_service.domain.port.in;

import java.util.UUID;

public interface AdjustStockCommand {

    void adjust(UUID aggregateId, long newAvailableQty);

}
