package br.com.orbity.ms_catalog_service_v1.domain.port.out;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;

public interface ProductEventPublisherPortOut {

    enum Type { CREATED, UPDATE, DELETE }

    void publishChanged (Product product, Type type);

}
