package br.com.orbity.ms_inventory_service.config;


import br.com.orbity.ms_inventory_service.application.process.ReservationSaga;
import br.com.orbity.ms_inventory_service.application.projector.StockProjectionHandler;
import br.com.orbity.ms_inventory_service.domain.port.out.SnapshotStorePortOut;
import br.com.orbity.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import br.com.orbity.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import br.com.orbity.ms_inventory_service.application.scheduler.SnapshotJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {


    // ========= Projector / Saga / Scheduler =========

    @Bean
    public StockProjectionHandler stockProjectionHandler(StockReadRepositoryPortOut readRepository) {
        return new StockProjectionHandler(readRepository);
    }

    @Bean
    public ReservationSaga reservationSaga(StockEventPublisherPortOut publisher) {
        return new ReservationSaga(publisher);
    }

    @Bean
    public SnapshotJob snapshotJob(
            SnapshotStorePortOut snapshots
    ) {
        // versão "light": job apenas com store; política em SnapshotJob
        return new SnapshotJob(snapshots);
    }
}