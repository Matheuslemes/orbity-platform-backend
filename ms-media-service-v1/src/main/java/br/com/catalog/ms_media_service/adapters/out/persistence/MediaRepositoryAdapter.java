package br.com.catalog.ms_media_service.adapters.out.persistence;

import br.com.catalog.ms_media_service.domain.model.MediaAsset;
import br.com.catalog.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaRepositoryAdapter implements MediaRepositoryPortOut {

    private final MediaMongoSpringData repository;

    @Override
    public MediaAsset save(MediaAsset asset) {

        log.info("[MediaRepositoryAdapter] - [save] IN -> id={}", asset.getId());

        var d = new MediaMongoDocument();
        d.setId(asset.getId().toString());
        d.setFileName(asset.getFilename());
        d.setContentType(asset.getContentType());
        d.setChecksum(asset.getChecksum());
        d.setStorageKey(asset.getStorageKey());
        d.setCreatedAt(asset.getCreatedAt());

        var saved = repository.save(d);
        log.info("[MediaRepositoryAdapter] - [save] OUT -> id={}", saved.getId());

        return MediaAsset.of(
                UUID.fromString(saved.getId()),
                saved.getFileName(),
                saved.getContentType(),
                saved.getSize() == null ? 0 : saved.getSize(),
                saved.getChecksum(),
                saved.getStorageKey(),
                saved.getCreatedAt()
        );
    }

    @Override
    public Optional<MediaAsset> findById(UUID id) {

        log.info("[MediaRepositoryAdapter] - [findById] IN -> id={}", id);

        return repository.findById(id.toString()).map(d ->
                MediaAsset.of(
                        UUID.fromString(d.getId()),
                        d.getFileName(),
                        d.getContentType(),
                        d.getSize() == null ? 0 : d.getSize(),
                        d.getChecksum(),
                        d.getStorageKey(),
                        d.getCreatedAt()
                )
        );
    }
}
