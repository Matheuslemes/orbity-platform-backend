package br.com.orbity.ms_media_service.adapters.out.persistence;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaRepositoryAdapter implements MediaRepositoryPortOut {

    private final MediaMongoSpringData repository;

    @Override
    public MediaAsset save(MediaAsset asset) {
        log.info("[MediaRepositoryAdapter] - [save] IN -> id={}", asset.getId());

        var d = new MediaMongoDocument();
        d.setId(asset.getId());                              // UUID direto
        d.setFileName(asset.getFilename());
        d.setContentType(asset.getContentType());
        d.setSize(asset.getSize());                          // << preencher size
        d.setChecksum(asset.getChecksum());
        d.setStorageKey(asset.getStorageKey());
        d.setCreatedAt(asset.getCreatedAt());                // se for OffsetDateTime, manter

        var saved = repository.save(d);
        log.info("[MediaRepositoryAdapter] - [save] OUT -> id={}", saved.getId());

        return MediaAsset.of(
                saved.getId(),                                // UUID direto
                saved.getFileName(),
                saved.getContentType(),
                saved.getSize() == null ? 0L : saved.getSize(),
                saved.getChecksum(),
                saved.getStorageKey(),
                saved.getCreatedAt()
        );
    }

    @Override
    public Optional<MediaAsset> findById(UUID id) {
        log.info("[MediaRepositoryAdapter] - [findById] IN -> id={}", id);

        return repository.findById(id).map(d ->
                MediaAsset.of(
                        d.getId(),
                        d.getFileName(),
                        d.getContentType(),
                        d.getSize() == null ? 0L : d.getSize(),
                        d.getChecksum(),
                        d.getStorageKey(),
                        d.getCreatedAt()
                )
        );
    }
}
