package br.com.orbity.ms_inventory_service.domain.port.in;

import java.util.UUID;

public interface UpdateStockCommand {

    void decrement(UUID aggregateId, long qty);

    void reserve(UUID aggregateId, long qty);

    void release(UUID aggregateId, long qty);

}
