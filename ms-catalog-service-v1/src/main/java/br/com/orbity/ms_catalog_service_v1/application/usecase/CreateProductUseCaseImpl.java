package br.com.orbity.ms_catalog_service_v1.application.usecase;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.port.in.CreateProductCommand;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductEventPublisherPortOut;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductCommand {

    private final ProductRepositoryPortOut repository;

    private final ProductEventPublisherPortOut publisher;

    @Override
    public Product create(Product product) {

        log.info("[CreateProductUseCase] - [create] IN -> {}", product);

        if (product == null) {
            log.error("[CreateProductUseCase] - [create] Product is null!");
            throw new IllegalArgumentException("Product is null!");
        }

        if (isBlank(product.sku()) || isBlank(product.name())) {
            log.error("[CreateProductUseCase] - [create] Missing required fields sku/name");
            throw new IllegalArgumentException("Missing required fields sku/name");
        }

        if (repository.existsBySku(product.sku())) {
            log.warn("[CreateProductUseCase] - [create] SKU already exists: {}", product.sku());
            throw new IllegalArgumentException("SKU já existente: " + product.sku());
        }

        Product saved = repository.save(product);
        log.info("[CreateProductUseCase] - [create] Saved product id={} sku={}", saved.id(), saved.sku());

        try {
            publisher.publishChanged(saved, ProductEventPublisherPortOut.Type.CREATED);
            log.info("[CreateProductUseCase] - [create] Published CREATED event for product id={}", saved.id());
        } catch (Exception ex) {
            log.error("[CreateProductUseCase] - [create] FAIL publish event id={} error={}",saved.id(), ex.getMessage(), ex);
        }

        log.info("[CreateProductUseCase] - [create] OUT <- {}", saved);

        return saved;

    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
