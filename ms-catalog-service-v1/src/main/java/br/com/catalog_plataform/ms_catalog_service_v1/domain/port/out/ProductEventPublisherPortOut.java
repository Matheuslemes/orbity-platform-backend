package br.com.catalog_plataform.ms_catalog_service_v1.domain.port.out;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.model.Product;

public interface ProductEventPublisherPortOut {

    enum Type { CREATED, UPDATE }

    void publishChanged (Product product, Type type);

}
