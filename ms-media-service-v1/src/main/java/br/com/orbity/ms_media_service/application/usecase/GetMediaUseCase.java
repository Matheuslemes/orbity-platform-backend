package br.com.orbity.ms_media_service.application.usecase;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.domain.port.in.GetMediaQuery;
import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import br.com.orbity.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMediaUseCase implements GetMediaQuery {

    private static final Duration DEFAULT_URL_TTL = Duration.ofMinutes(10);

    private final MediaRepositoryPortOut repository;
    private final BlobStoragePortOut storage;

    @Override
    public Optional<MediaAsset> byId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id obrigatório");
        }

        log.info("[GetMediaUseCase] - [byId] IN -> id={}", id);

        var out = repository.findById(id);

        log.info("[GetMediaUseCase] - [byId] OUT -> id={} present={}", id, out.isPresent());
        return out;
    }

    @Override
    public Optional<String> presignedUrl(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id obrigatório");
        }

        log.info("[GetMediaUseCase] - [presignedUrl] IN -> id={}", id);

        return byId(id)
                .map(asset -> {
                    String blobName = asset.getStorageKey(); // aqui storageKey = blobName
                    log.debug("[GetMediaUseCase] - [presignedUrl] gerar URL para blobName={}", blobName);
                    var url = storage.presignedGet(blobName, DEFAULT_URL_TTL);
                    return url.toString();
                });
    }
}
