package br.com.catalog.ms_search_service.application.usecase;

import br.com.catalog.ms_search_service.domain.port.in.ReindexPortIn;
import br.com.catalog.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReindexUseCase implements ReindexPortIn {

    private final SearchRepositoryPortOut repository;

    public ReindexUseCase(SearchRepositoryPortOut repository) {
        this.repository =repository;
    }


    @Override
    public void reindexAll() {

        log.info("[ReindexUseCase] - [reindexAll] IN");
        repository.reindexAll();
        log.info("[ReindexUseCase] - [reindexAll] OUT");
        
    }
}
