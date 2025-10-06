package br.com.orbity.ms_catalog_service_v1.domain.port.in;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetProductQuery {

    Optional<Product> byId(UUID id);

    List<Product> list(int page, int size);

}
