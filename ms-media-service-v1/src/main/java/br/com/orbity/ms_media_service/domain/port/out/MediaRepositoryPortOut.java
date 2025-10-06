package br.com.orbity.ms_media_service.domain.port.out;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;

import java.util.Optional;
import java.util.UUID;

public interface MediaRepositoryPortOut {

    MediaAsset save(MediaAsset asset);

    Optional<MediaAsset> findById(UUID id);

}
