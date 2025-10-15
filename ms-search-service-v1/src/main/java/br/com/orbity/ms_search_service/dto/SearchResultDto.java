package br.com.orbity.ms_search_service.dto;

import java.util.List;

public record SearchResultDto(
        String query,
        int page,
        int size,
        List<ProductHitDto> hits
) {
    public SearchResultDto {
        query = (query == null) ? null : query.trim();
        page  = Math.max(0, page);
        size  = Math.max(1, size);
        hits  = (hits == null) ? List.of() : List.copyOf(hits);
    }
}