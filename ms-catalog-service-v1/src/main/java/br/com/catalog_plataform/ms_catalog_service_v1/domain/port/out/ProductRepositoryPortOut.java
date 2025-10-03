package br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPortOut {

    boolean existsBySku (String sku);

    Optional<Product> findById (UUID id);

    List<Product> findAll (int page, int size);

    Product save (Product product);

    void delete (UUID id);

}
