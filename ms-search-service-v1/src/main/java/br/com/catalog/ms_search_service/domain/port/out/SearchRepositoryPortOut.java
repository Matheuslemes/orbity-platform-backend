package br.com.catalog.ms_search_service.domain.port.out;

import br.com.catalog.ms_search_service.domain.model.ProductIndex;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SearchRepositoryPortOut {

    void index(ProductIndex doc);

    void bulkIndex(List<ProductIndex> docs);

    Optional<ProductIndex> findById(UUID id);

    List<ProductIndex> search(String query, int page, int size);

    void reindexAll(); // estratégeria definida pelo adapter (pode ler de outra fonte)
}
