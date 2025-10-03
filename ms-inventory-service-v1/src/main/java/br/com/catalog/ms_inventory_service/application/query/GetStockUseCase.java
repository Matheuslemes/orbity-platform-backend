package br.com.catalog.ms_inventory_service.application.query;

import br.com.catalog.ms_inventory_service.domain.model.StockAggregate;
import br.com.catalog.ms_inventory_service.domain.port.in.GetStockQuery;
import br.com.catalog.ms_inventory_service.domain.port.out.StockReadRepositoryPortOut;
import br.com.catalog.ms_inventory_service.util.StockAggregateLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetStockUseCase implements GetStockQuery {

    private final StockReadRepositoryPortOut readRepository;
    private final StockAggregateLoader loader;

    public GetStockUseCase(StockReadRepositoryPortOut readRepository,
                           StockAggregateLoader loader) {
        this.readRepository = readRepository;
        this.loader = loader;
    }

    @Override
    public Optional<StockAggregate> byId(UUID aggregateId) {

        if (aggregateId == null) return Optional.empty();

        try {
            // Carrega do snapshot + eventos (CQRS/ES)
            var agg = loader.loadAggregate(aggregateId);
            return Optional.ofNullable(agg);
        } catch (Exception e) {
            // não encontrado ou erro de rehydrate → trata como vazio
            return Optional.empty();
        }
    }

    @Override
    public Optional<StockReadRepositoryPortOut.StockRead> bySku(String sku) {

        var normalized = normalizeSku(sku);
        if (normalized == null) return Optional.empty();

        return readRepository.findBySku(normalized);
    }

    @Override
    public List<StockReadRepositoryPortOut.StockRead> list(int page, int size) {

        int p = Math.max(0, page);
        int s = clamp(size, 1, 200); // limite de segurança

        return readRepository.findAll(p, s);

    }

    //helpers
    private static String normalizeSku(String sku) {

        if (sku == null) return null;
        var s = sku.trim();

        return s.isEmpty() ? null : s;

    }

    private static int clamp(int v, int min, int max) {

        return Math.max(min, Math.min(max, v));

    }
}