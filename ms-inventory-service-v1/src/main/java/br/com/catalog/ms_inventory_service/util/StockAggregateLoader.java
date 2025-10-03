package br.com.catalog.ms_inventory_service.util;

import br.com.catalog.ms_inventory_service.domain.model.StockAggregate;
import br.com.catalog.ms_inventory_service.domain.port.out.EventStorePortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.SnapshotStorePortOut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockAggregateLoader {

    private final EventStorePortOut events;
    private final SnapshotStorePortOut snapshots;
    private final ObjectMapper om;

    public StockAggregateLoader(EventStorePortOut events, SnapshotStorePortOut snapshots, ObjectMapper om) {
        this.events = events;
        this.snapshots = snapshots;
        this.om = om;
    }

    public StockAggregate loadAggregate(UUID id) {
        long snapVersion = 0L;
        StockAggregate agg;

        var snapOpt = snapshots.find(id);
        if (snapOpt.isPresent()) {

            var snap = snapOpt.get();
            snapVersion = snap.version();

            try {
                JsonNode node = om.readTree(snap.stateJson());
                agg = StockAggregate.rehydrate(
                        id,
                        node.path("sku").asText("unknown"),
                        node.path("version").asLong(snapVersion),
                        node.path("availableQty").asLong(0L),
                        node.path("reservedQty").asLong(0L)
                );
            } catch (Exception e) {
                throw new IllegalArgumentException("Snapshot inválido para aggregateId=" + id, e);
            }

        } else {
            agg = StockAggregate.rehydrate(id, "unknown", 0, 0, 0);
        }

        var stored = events.loadEvents(id);

        for (var se : stored) {
            final long evVersion = java.util.Optional.ofNullable(se.version()).orElse(0L);
            if (evVersion <= snapVersion) continue;
            var ev = EventSerde.deserialize(se.eventType(), se.payload(), om);

            agg.apply(ev);

        }

        agg.clearUncommittedEvents();

        return agg;
    }
}