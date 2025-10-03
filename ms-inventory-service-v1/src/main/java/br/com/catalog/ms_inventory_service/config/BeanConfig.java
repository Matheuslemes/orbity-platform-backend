package br.com.catalog.ms_inventory_service.config;


import br.com.catalog.ms_inventory_service.application.process.ReservationSaga;
import br.com.catalog.ms_inventory_service.application.projector.StockProjectionHandler;
import br.com.catalog.ms_inventory_service.application.query.GetStockUseCase;
import br.com.catalog.ms_inventory_service.application.usecase.AdjustStockUseCase;
import br.com.catalog.ms_inventory_service.application.usecase.CreateStockUseCase;
import br.com.catalog.ms_inventory_service.application.usecase.UpdateStockUseCase;
import br.com.catalog.ms_inventory_service.domain.port.in.AdjustStockCommand;
import br.com.catalog.ms_inventory_service.domain.port.in.CreateStockCommand;
import br.com.catalog.ms_inventory_service.domain.port.in.GetStockQuery;
import br.com.catalog.ms_inventory_service.domain.port.in.UpdateStockCommand;
import br.com.catalog.ms_inventory_service.domain.port.out.EventStorePortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.SnapshotStorePortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import br.com.catalog.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import br.com.catalog.ms_inventory_service.util.StockAggregateLoader;
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
    public br.com.catalog.ms_inventory_service.application.scheduler.SnapshotJob snapshotJob(
            SnapshotStorePortOut snapshots
    ) {
        // versão "light": job apenas com store; política em SnapshotJob
        return new br.com.catalog.ms_inventory_service.application.scheduler.SnapshotJob(snapshots);
    }
}