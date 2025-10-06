package br.com.orbity.ms_media_service.domain.port.in;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;

import java.util.Optional;
import java.util.UUID;

public interface GetMediaQuery {

    Optional<MediaAsset> byId(UUID id);

    Optional<String> presignedUrl(UUID id); // opcional url de acesso temporária

}
