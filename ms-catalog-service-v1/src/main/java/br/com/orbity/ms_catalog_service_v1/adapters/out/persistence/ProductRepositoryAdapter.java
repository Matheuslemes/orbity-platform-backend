package br.com.orbity.ms_catalog_service_v1.adapters.out.persistence;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.port.out.ProductRepositoryPortOut;
import br.com.orbity.ms_catalog_service_v1.mapping.ProductMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPortOut {

    private final ProductMongoSpringData mongo;
    private final ProductMongoMapper mapper;

    @Override
    public boolean existsBySku(String sku) {

        return mongo.existsBySku(sku);

    }

    @Override
    public Optional<Product> findById(UUID id) {

        return mongo.findById(UUID.fromString(id.toString())).map(mapper::toProductDomain);

    }

    @Override
    public List<Product> findAll(int page, int size) {

        return mongo.findAll(PageRequest.of(page, size))
                .map(mapper::toProductDomain)
                .getContent();

    }

    @Override
    public Product save(Product product) {

        var doc = mapper.toProductDocument(product);

        Instant now = Instant.now();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);
        doc.setUpdatedAt(now);

        var saved = mongo.save(doc);
        return mapper.toProductDomain(saved);

    }

    @Override
    public void deleteById(UUID id) {

        mongo.deleteById(UUID.fromString(id.toString()));

    }
}
