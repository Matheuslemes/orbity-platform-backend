package br.com.orbity.ms_catalog_service_v1.domain.port.in;

import java.util.UUID;

public interface DeleteProductCommand {

    void delete(UUID id);

}
