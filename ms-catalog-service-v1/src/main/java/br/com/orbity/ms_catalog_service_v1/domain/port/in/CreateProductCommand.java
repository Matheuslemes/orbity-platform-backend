package br.com.orbity.ms_catalog_service_v1.domain.port.in;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;

public interface CreateProductCommand {

    Product create(Product product);

}
