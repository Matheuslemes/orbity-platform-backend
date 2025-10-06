package br.com.orbity.ms_catalog_service_v1.domain.model;

import java.util.UUID;

public record Variant(
        UUID id,
        String sku,
        String name,
        String attributesJson
) {
    public Variant {

        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("Variant SKU é obrigatório");

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Variant name é obrigatório");
    }

    public static Variant of(UUID id, String sku, String name, String attributesJson) {

        return new Variant(id != null ? id : UUID.randomUUID(), sku, name, attributesJson);
    }

    public Variant witName(String newName) {

        return new Variant(
                this.id,
                this.sku,
                newName,
                this.attributesJson);
    }

    public Variant withAttributesJson(String json) {

        return new Variant(
                this.id,
                this.sku,
                this.name,
                json);
    }

}
