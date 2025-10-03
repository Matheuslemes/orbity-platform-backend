package br.com.catalog.ms_inventory_service.application.usecase;


import br.com.catalog.ms_inventory_service.domain.model.StockAggregate;
import br.com.catalog.ms_inventory_service.domain.port.in.CreateStockCommand;
import br.com.catalog.ms_inventory_service.domain.port.out.EventStorePortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class CreateStockUseCase implements CreateStockCommand {

    private final EventStorePortOut eventStore;
    private final StockEventPublisherPortOut publisher;

    public CreateStockUseCase(EventStorePortOut eventStore, StockEventPublisherPortOut publisher) {
        this.eventStore = eventStore;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public StockAggregate create(String sku, long initialQty) {

        log.info("[CreateStockUseCase] - [create] IN -> sku={} initialQty={}", sku, initialQty);

        // validações
        if (StringUtils.isBlank(sku)) {

            log.error("[CreateStockUseCase] - [create] ERR -> sku blank");
            throw new IllegalArgumentException("sku is required");

        }

        if (initialQty < 0) {

            log.error("[CreateStockUseCase] - [create] ERR -> initialQty < 0");
            throw new IllegalArgumentException("initialQty must be >= 0");

        }

        var id = UUID.randomUUID();
        var agg = StockAggregate.createNew(id, sku, 0);
        if (initialQty > 0) {
            agg.adjust(initialQty); // gera StockAdjusted
        }

        var events = agg.getUncommittedEvents();

        if (!events.isEmpty()) {

            long expectedVersion = 0L; // agregado novo
            eventStore.append(agg.getId(), expectedVersion, events);
            events.forEach(publisher::publish);
            agg.clearUncommittedEvents();

        }

        log.info("[CreateStockUseCase] - [create] OUT -> id={} sku={} initialQty={}", agg.getId(), sku, initialQty);

        return agg;
    }

}