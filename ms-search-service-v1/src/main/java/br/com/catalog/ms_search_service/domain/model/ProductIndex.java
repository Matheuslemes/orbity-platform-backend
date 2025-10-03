package br.com.catalog.ms_search_service.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProductIndex(
        UUID id,
        String sku,
        String name,
        String description,
        List<String> categories,
        List<String> tags,
        Double price,
        Long availableQty,
        OffsetDateTime updatedAt

) {
    public static ProductIndex of(
            UUID id, String sku, String name, String description, List<String> categories,
            List<String> tags, Double price, Long availableQty, OffsetDateTime updatedAt) {
        return new ProductIndex(id, trim(sku), trim(name), trim(description), categories, tags, price, safeLong(availableQty), updatedAt);
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static Long safeLong(Long v) { return v == null ? 0l : v; }
}
