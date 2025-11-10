package br.com.orbity.ms_catalog_service_v1.application.usecase;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.port.in.DeleteProductCommand;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductEventPublisherPortOut;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductUseCaseImpl implements DeleteProductCommand {

    private final ProductRepositoryPortOut repository;
    private final ProductEventPublisherPortOut publisher;

    @Override
    public void delete(UUID id) {
        log.info("[DeleteProductUseCase] - [delete] IN -> id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        Product current = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[DeleteProductUseCase] - [delete] Product not found id={}", id);
                    return new NoSuchElementException("Product not found: " + id);
                });

        try {
            repository.deleteById(id);
            log.info("[DeleteProductUseCase] - [delete] Deleted product id={} sku={}", current.id(), current.sku());
        } catch (Exception ex) {
            log.error("[DeleteProductUseCase] - [delete] FAIL delete id={} error={}", id, ex.getMessage(), ex);
            throw ex;
        }

        try {
            publisher.publishChanged(current, ProductEventPublisherPortOut.Type.DELETE);
            log.info("[DeleteProductUseCase] - [delete] Published DELETED event for id={}", id);
        } catch (Exception ex) {
            log.error("[DeleteProductUseCase] - [delete] FAIL publish event id={} error={}", id, ex.getMessage(), ex);
        }

        log.info("[DeleteProductUseCase#delete] OUT <- id={}", id);
    }
}
