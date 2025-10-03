package br.com.catalog_plataform.ms_catalog_service_v1.config;

import br.com.catalog_plataform.ms_catalog_service_v1.application.usecase.CreateProductUseCaseImpl;
import br.com.catalog_plataform.ms_catalog_service_v1.application.usecase.GetProductUseCaseImpl;
import br.com.catalog_plataform.ms_catalog_service_v1.application.usecase.UpdateProductUseCaseImpl;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.CreateProductCommand;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.GetProductQuery;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.UpdateProductCommand;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out.ProductEventPublisherPortOut;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateProductCommand createProductUseCase(
            ProductRepositoryPortOut repository,
            ProductEventPublisherPortOut publisher) {

        return new CreateProductUseCaseImpl(repository, publisher);
    }

    @Bean
    public UpdateProductCommand updateProductUseCase(
            ProductRepositoryPortOut repository,
            ProductEventPublisherPortOut publisher) {

        return new UpdateProductUseCaseImpl(repository, publisher);
    }

    @Bean
    public GetProductQuery getProductUseCase(ProductRepositoryPortOut repository) {

        return new GetProductUseCaseImpl(repository);
    }

}
