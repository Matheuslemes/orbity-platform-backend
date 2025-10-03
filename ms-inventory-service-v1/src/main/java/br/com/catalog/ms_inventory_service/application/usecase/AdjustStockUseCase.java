package br.com.catalog.ms_inventory_service.application.usecase;


import br.com.catalog.ms_inventory_service.domain.port.in.AdjustStockCommand;
import br.com.catalog.ms_inventory_service.domain.port.out.EventStorePortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import br.com.catalog.ms_inventory_service.util.StockAggregateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class AdjustStockUseCase implements AdjustStockCommand {

    private final StockAggregateLoader loader;
    private final EventStorePortOut events;
    private final StockEventPublisherPortOut publisher;

    public AdjustStockUseCase(StockAggregateLoader loader, EventStorePortOut events, StockEventPublisherPortOut publisher) {
        this.loader = loader;
        this.events = events;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public void adjust(UUID aggregateId, long newAvailableQty) {

        log.info("[AdjustStockUseCase] - [adjust] IN -> id={} newAvailableQty={}", aggregateId, newAvailableQty);

        // validações
        if (aggregateId == null) {

            log.error("[AdjustStockUseCase] - [adjust] ERR -> aggregateId is null");
            throw new IllegalArgumentException("aggregateId is required");

        }

        if (newAvailableQty < 0) {

            log.error("[AdjustStockUseCase] - [adjust] ERR -> newAvailableQty < 0");
            throw new IllegalArgumentException("newAvailableQty must be >= 0");

        }

        var agg = loader.loadAggregate(aggregateId);
        agg.adjust(newAvailableQty);

        var uncommitted = agg.getUncommittedEvents();
        if (uncommitted.isEmpty()) {
            log.info("[AdjustStockUseCase] - [adjust] OUT -> no-op (no events)");

            return;

        }

        long baseVersion = agg.getVersion() - uncommitted.size();
        events.append(agg.getId(), baseVersion, uncommitted);
        uncommitted.forEach(publisher::publish);
        agg.clearUncommittedEvents();

        log.info("[AdjustStockUseCase] - [adjust] OUT -> id={} baseVersion={}", aggregateId, baseVersion);

    }

}