package br.com.orbity.ms_catalog_service_v1.adapters.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductMongoSpringData extends MongoRepository<ProductDocument, String> {

    boolean existsBySku(String sku);


}
