package br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.model.Product;

import java.util.UUID;

public interface UpdateProductCommand {

    Product update(UUID id, Product changes);

}
