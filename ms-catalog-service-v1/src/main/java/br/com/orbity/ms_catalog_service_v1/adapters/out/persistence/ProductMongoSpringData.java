package br.com.orbity.ms_catalog_service_v1.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ProductMongoSpringData extends MongoRepository<ProductDocument, UUID> {

    boolean existsBySku(String sku);

    Page<ProductDocument> findAll(Pageable pageable);

    java.util.Optional<ProductDocument> findBySlug(String slug);

    boolean existsByVariantsSku(String sku);


}
