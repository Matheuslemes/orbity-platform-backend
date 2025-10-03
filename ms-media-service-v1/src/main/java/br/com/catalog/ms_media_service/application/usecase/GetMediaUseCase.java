package br.com.catalog.ms_media_service.application.usecase;

import br.com.catalog.ms_media_service.domain.model.MediaAsset;
import br.com.catalog.ms_media_service.domain.port.in.GetMediaQuery;
import br.com.catalog.ms_media_service.domain.port.out.BlobStoragePortOut;
import br.com.catalog.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class GetMediaUseCase implements GetMediaQuery {

    private final MediaRepositoryPortOut repository;
    private final BlobStoragePortOut storage;

    public GetMediaUseCase(MediaRepositoryPortOut repository, BlobStoragePortOut storage) {
        this.repository = repository;
        this.storage = storage;
    }

    @Override
    public Optional<MediaAsset> byId(UUID id) {

        log.info("[GetMediaUseCase] - [byId] IN -> id={}", id);
        if (id == null) throw new IllegalArgumentException("id obrigatório");
        var out = repository.findById(id);
        log.info("[GetMediaUseCase] - [byId] OUT -> present={}", out.isPresent());

        return out;
    }

    @Override
    public Optional<String> presignedUrl(UUID id) {

        log.info("[GetMediaUseCase] - [presignedUrl] IN -> id={}", id);

        return byId(id).map(a -> storage.presignedGetUrl(a.getStorageKey(), Duration.ofMinutes(10)));
    }
}
