package br.com.orbity.ms_catalog_service_v1.domain.model;

public record Media(String url, String label, boolean primary) {

    public Media {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url é obrigatória");
        }

    }
}
