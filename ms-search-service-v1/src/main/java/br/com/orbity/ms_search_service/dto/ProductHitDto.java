package br.com.orbity.ms_search_service.dto;

import java.util.UUID;

public record ProductHitDto (
        UUID id,
        String sku,
        String name,
        String description,
        Double price // ajuste o tipo conforme seu ProductIndex
) {}
