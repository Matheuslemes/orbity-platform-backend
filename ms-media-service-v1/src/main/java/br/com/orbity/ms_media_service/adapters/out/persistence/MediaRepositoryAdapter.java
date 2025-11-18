package br.com.orbity.ms_media_service.adapters.out.persistence;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import br.com.orbity.ms_media_service.mapping.MediaMongoMapper;
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
    private final MediaMongoMapper mapper;

    @Override
    public MediaAsset save(MediaAsset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset não pode ser nulo");
        }

        log.info("[MediaRepositoryAdapter] - [save] IN -> id={}", asset.getId());

        MediaMongoDocument document = mapper.toDocument(asset);

        MediaMongoDocument saved = repository.save(document);

        log.info("[MediaRepositoryAdapter] - [save] OUT -> id={}", saved.getId());

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MediaAsset> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id não pode ser nulo");
        }

        log.info("[MediaRepositoryAdapter] - [findById] IN -> id={}", id);

        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
