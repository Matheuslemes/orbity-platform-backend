package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import br.com.catalog.ms_inventory_service.domain.port.out.SnapshotStorePortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SnapshotRepositoryAdapter implements SnapshotStorePortOut {

    private final SnapshotSpringRepository repo;

    public SnapshotRepositoryAdapter(SnapshotSpringRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Snapshot> find(UUID aggregateId) {

        return repo.findById(aggregateId)
                .map(e -> new Snapshot(e.getAggregateId(), e.getVersion(), e.getSnapshotState()));

    }

    @Override
    public void save(Snapshot snapshot) {

        var e = new SnapshotJpaEntity();
        e.setAggregateId(snapshot.aggregateId());
        e.setVersion(snapshot.version());
        e.setSnapshotState(snapshot.stateJson());
        e.setTakenAt(OffsetDateTime.now());
        repo.save(e);

        log.debug("Saved snapshot v{} for {}", snapshot.version(), snapshot.aggregateId());

    }
}