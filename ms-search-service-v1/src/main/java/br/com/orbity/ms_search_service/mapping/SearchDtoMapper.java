package br.com.orbity.ms_search_service.mapping;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;
import br.com.orbity.ms_search_service.dto.ProductHitDto;
import br.com.orbity.ms_search_service.dto.SearchResultDto;
import org.jetbrains.annotations.Contract;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchDtoMapper {

    // converte o documento indexado no search para o dto exposto pela api
    // mantém o dominio independente da camada de transporte
    public ProductHitDto toHit(ProductIndex p) {

        if (p == null) return null;

        return new ProductHitDto(
                p.id(),
                trim(p.sku()),
                trim(p.name()),
                trim(p.description()),
                p.price()
        );
    }

    // constroi o envelope de resultado (query/page/size + lista de hits mapeados)
    public SearchResultDto toResult(String query, int page, int size, List<ProductIndex> docs) {

        List<ProductHitDto> hits = (docs == null) ? List.of()
                : docs.stream().map(this::toHit).toList();

        return new SearchResultDto(query, page, size, hits);
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    private String trim(String s) { return s == null ? null : s.trim(); }

}
