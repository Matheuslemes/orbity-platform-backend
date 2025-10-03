package br.com.catalog_plataform.ms_catalog_service_v1.application.usecase;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.model.Product;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.UpdateProductCommand;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out.ProductEventPublisherPortOut;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UpdateProductUseCaseImpl implements UpdateProductCommand {

    private final ProductRepositoryPortOut repository;

    private final ProductEventPublisherPortOut publisher;

    @Override
    public Product update(UUID id, Product changes) {

        log.info("[UpdateProductUseCase] - [update] IN -> id={} changes={}", id, changes);

        if (id == null || changes == null)
            throw new IllegalArgumentException("id/changes cannot be null");

        Product current = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[UpdateProductUseCase] - [update] Product not found id={}", id);
                    return new NoSuchElementException("Product not found: " + id);
                });

        current.updateBasics(changes.name(), changes.description(), changes.slug());
        current.replaceVariants(changes.variants());
        log.debug("[UpdateProductUseCase] - [update] Updated fields for id={}", id);

        Product saved = repository.save(current);
        log.info("[UpdateProductUseCase] - [update] Saved product id={} sku={}", saved.id(), saved.sku());

        try {
            publisher.publishChanged(saved, ProductEventPublisherPortOut.Type.UPDATE);
            log.info("[UpdateProductUseCase] - [update] Published UPDATED event for id={}", saved.id());
        } catch (Exception ex) {
            log.error("[UpdateProductUseCase] - [update] FAIL publish event id={} error={}", saved.id(), ex.getMessage(), ex);
        }

        log.info("[UpdateProductUseCase#update] OUT <- {}", saved);

        return saved;

    }
}
