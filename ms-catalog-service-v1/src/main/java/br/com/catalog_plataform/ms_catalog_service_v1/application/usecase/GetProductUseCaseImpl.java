package br.com.catalog_plataform.ms_catalog_service_v1.application.usecase;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.model.Product;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.GetProductQuery;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetProductUseCaseImpl implements GetProductQuery {

    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;


    private final ProductRepositoryPortOut repository;

    @Override
    public Optional<Product> byId(UUID id) {

        log.info("[GetProductUseCase] - [byId] IN -> id={}", id);
        if (id == null) throw new IllegalArgumentException("id is null!");

        Optional<Product> result = repository.findById(id);
        log.info("[GetProductUseCase] - [byId] OUT <- found={}", result.isPresent());

        return result;

    }

    @Override
    public List<Product> list(int page, int size) {

        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size <= 0 ? DEFAULT_SIZE : size), MAX_SIZE);

        if (p != page || s != size)
            log.info("[GetProductUseCase] - [list] normalized pagination from page={} size={} to page={} size={}",
                    page, size, p, s);

        log.info("[GetProductUseCase#list] IN -> page={} size={}", page, size);
        List<Product> products = repository.findAll(page, size);
        log.info("[GetProductUseCase#list] OUT <- found={}", products.size());

        return products;

    }
}
