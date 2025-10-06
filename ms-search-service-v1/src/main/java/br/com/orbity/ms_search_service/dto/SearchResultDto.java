package br.com.orbity.ms_search_service.dto;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;

import java.awt.*;
import java.util.List;

public record SearchResultDto(
        String query,
        int page,
        int size,
        List<ProductIndex> hits
) { }
