package br.com.catalog.ms_media_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MediaDto (
        UUID id,
        String filename,
        String contentType,
        long size,
        String url,
        OffsetDateTime createdAt

) { }
