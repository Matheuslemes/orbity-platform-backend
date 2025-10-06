package br.com.orbity.ms_catalog_service_v1.adapters.out.messaging.payload;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;

public record ProductChangePayload(
        String eventType,
        String id,
        String sku,
        String name,
        String description,
        String slug,
        Long version,
        String eventId
) {
    public static ProductChangePayload from(Product p, String eventType) {

        return new ProductChangePayload(
                eventType,
                p.id() != null ? p.id().toString() : null,
                p.sku(),
                p.name(),
                p.description(),
                p.slug(),
                p.version(),
                null
        );

    }

    public static ProductChangePayload from(Product p, String eventType, String eventId) {

        return new ProductChangePayload(
                eventType,
                p.id() != null ? p.id().toString() : null,
                p.sku(),
                p.name(),
                p.description(),
                p.slug(),
                p.version(),
                eventId
        );

    }
}
