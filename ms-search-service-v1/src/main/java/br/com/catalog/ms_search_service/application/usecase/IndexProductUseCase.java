package br.com.catalog.ms_search_service.application.usecase;

import br.com.catalog.ms_search_service.domain.model.ProductIndex;
import br.com.catalog.ms_search_service.domain.port.in.IndexProductPortIn;
import br.com.catalog.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexProductUseCase implements IndexProductPortIn {

    private final SearchRepositoryPortOut repository;

    public IndexProductUseCase(SearchRepositoryPortOut repository) {
        this.repository = repository;
    }

    @Override
    public void index(ProductIndex doc) {

        log.info("[IndexProductUseCase] - [index] IN -> sku={} id={}", doc.sku(), doc.id());
        repository.index(doc);
        log.info("[IndexProductUseCase] - [index] OUT -> sku={} id={}", doc.sku(), doc.id());

    }
}
