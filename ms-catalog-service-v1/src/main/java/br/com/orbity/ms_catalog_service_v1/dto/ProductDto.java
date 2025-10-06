package br.com.orbity.ms_catalog_service_v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(name = "Product")
public record ProductDto(
        UUID id,
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        String slug,
        @Valid List<VariantDto> variants,
        Long version
) {}

