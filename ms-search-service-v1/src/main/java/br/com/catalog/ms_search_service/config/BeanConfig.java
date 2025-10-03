package br.com.catalog.ms_search_service.config;

import br.com.catalog.ms_search_service.application.usecase.IndexProductUseCase;
import br.com.catalog.ms_search_service.application.usecase.ReindexUseCase;
import br.com.catalog.ms_search_service.domain.port.in.IndexProductPortIn;
import br.com.catalog.ms_search_service.domain.port.in.ReindexPortIn;
import br.com.catalog.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public IndexProductPortIn indexProductUseCase(SearchRepositoryPortOut repository) {

        return new IndexProductUseCase(repository);
    }

    @Bean
    public ReindexPortIn reindexUseCase(SearchRepositoryPortOut repository) {
        return new ReindexUseCase(repository);
    }
}
