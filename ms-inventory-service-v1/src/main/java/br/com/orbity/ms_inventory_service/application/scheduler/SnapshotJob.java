package br.com.orbity.ms_inventory_service.application.scheduler;

import br.com.orbity.ms_inventory_service.domain.port.out.SnapshotStorePortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Job responsável por acionar a política de snapshots de agregados.
 * Esta implementação é propositalmente "light": não lista agregados.
 * Em produção, um componente (ex.: admin task) deve descobrir os aggregates
 * candidatos e chamar {@link #snapshotIfDue(UUID, long, long, long)}.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "inventory.snapshot", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SnapshotJob {

    private final SnapshotStorePortOut snapshots;

    public SnapshotJob(SnapshotStorePortOut snapshots) {
        this.snapshots = snapshots;
    }

    @Scheduled(cron = "${inventory.snapshot.cron:0 */30 * * * *}")
    public void rotateSnapshots() {
        // PONTO DE EXTENSÃO:
        // 1) Descobrir candidatos a snapshot (ex.: consulta no event_store por aggregates “quentes”,
        //    tabela auxiliar, ou uma fila administrada).
        // 2) Para cada candidato, recupere: aggregateId, currentVersion e lastSnapshotVersion,
        //    então chame snapshotIfDue(...).
        //
        // Mantemos esse hook sem no-op silencioso para ficar claro o próximo passo.
        log.debug("[SnapshotJob] rotateSnapshots disparado (nenhuma enumeração de aggregates nesta implementação).");
    }


    public boolean snapshotIfDue(UUID aggregateId, long currentVersion, long lastSnapshotVersion, long threshold) {

        if (aggregateId == null) {
            log.warn("[SnapshotJob] aggregateId nulo; ignorando.");
            return false;
        }

        if (threshold <= 0) {
            log.warn("[SnapshotJob] threshold inválido ({}); ignorando aggregateId={}.", threshold, aggregateId);
            return false;
        }

        long delta = Math.max(0, currentVersion - Math.max(0, lastSnapshotVersion));
        boolean due = delta >= threshold;

        log.debug("[SnapshotJob] aggregateId={} currentVersion={} lastSnapshotVersion={} delta={} threshold={} due={}",
                aggregateId, currentVersion, lastSnapshotVersion, delta, threshold, due);

        return due;

    }
}
