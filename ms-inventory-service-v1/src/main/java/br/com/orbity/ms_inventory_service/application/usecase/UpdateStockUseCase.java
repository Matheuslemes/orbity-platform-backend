package br.com.orbity.ms_inventory_service.application.usecase;

import br.com.orbity.ms_inventory_service.domain.model.StockAggregate;
import br.com.orbity.ms_inventory_service.domain.port.in.UpdateStockCommand;
import br.com.orbity.ms_inventory_service.domain.port.out.EventStorePortOut;
import br.com.orbity.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import br.com.orbity.ms_inventory_service.util.StockAggregateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
public class UpdateStockUseCase implements UpdateStockCommand {

    private final EventStorePortOut events;
    private final StockAggregateLoader loader;
    private final StockEventPublisherPortOut publisher;

    public UpdateStockUseCase(EventStorePortOut events, StockAggregateLoader loader, StockEventPublisherPortOut publisher) {
        this.events = events;
        this.loader = loader;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public void decrement(UUID aggregateId, long qty) {

        log.info("[UpdateStockUseCase] - [decrement] IN -> id={} qty={}", aggregateId, qty);

        validateIdAndQty(aggregateId, qty);
        applyAndPersist(aggregateId, agg -> agg.decrement(qty));

        log.info("[UpdateStockUseCase] - [decrement] OUT -> id={} qty={}", aggregateId, qty);
    }

    @Override
    @Transactional
    public void reserve(UUID aggregateId, long qty) {

        log.info("[UpdateStockUseCase] - [reserve] IN -> id={} qty={}", aggregateId, qty);

        validateIdAndQty(aggregateId, qty);
        applyAndPersist(aggregateId, agg -> agg.reserve(qty));

        log.info("[UpdateStockUseCase] - [reserve] OUT -> id={} qty={}", aggregateId, qty);
    }

    @Override
    @Transactional
    public void release(UUID aggregateId, long qty) {

        log.info("[UpdateStockUseCase] - [release] IN -> id={} qty={}", aggregateId, qty);

        validateIdAndQty(aggregateId, qty);
        applyAndPersist(aggregateId, agg -> agg.release(qty));

        log.info("[UpdateStockUseCase] - [release] OUT -> id={} qty={}", aggregateId, qty);

    }

    //helpers
    private void applyAndPersist(UUID aggregateId, Consumer<StockAggregate> action) {

        var agg = loader.loadAggregate(aggregateId);
        action.accept(agg);

        var uncommitted = agg.getUncommittedEvents();

        if (uncommitted.isEmpty()) {

            log.info("[UpdateStockUseCase] - [applyAndPersist] NO-OP -> id={}", aggregateId);
            return;

        }

        long baseVersion = agg.getVersion() - uncommitted.size();
        events.append(agg.getId(), baseVersion, uncommitted);
        uncommitted.forEach(publisher::publish);
        agg.clearUncommittedEvents();

    }

    private static void validateIdAndQty(UUID id, long qty) {

        if (id == null) {
            throw new IllegalArgumentException("aggregateId is required");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be > 0");
        }

    }

}