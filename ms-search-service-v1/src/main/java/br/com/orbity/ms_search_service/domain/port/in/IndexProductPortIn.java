package br.com.orbity.ms_search_service.domain.port.in;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;

public interface IndexProductPortIn {

    void index(ProductIndex doc);
}
