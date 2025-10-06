package br.com.orbity.ms_inventory_service.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface SnapshotStorePortOut {

    record Snapshot(UUID aggregateId, long version, String stateJson) {}

    Optional<Snapshot> find(UUID aggregateId);

    void save(Snapshot snapshot);
}
