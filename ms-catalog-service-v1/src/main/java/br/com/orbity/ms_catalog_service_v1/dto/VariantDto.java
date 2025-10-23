package br.com.orbity.ms_catalog_service_v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "Variant")
public record VariantDto(

        UUID id,
        @NotBlank String sku,
        @NotBlank String name,
        String attributesJson

) { }

