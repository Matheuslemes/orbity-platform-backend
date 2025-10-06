package br.com.orbity.ms_media_service.mapping;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.dto.MediaDto;
import org.springframework.stereotype.Component;

@Component
public class MediaDtoMapper {

    public MediaDto toDto(MediaAsset a) {

        if (a == null) return null;

        return  new MediaDto(
                a.getId(),
                a.getFilename(),
                a.getContentType(),
                a.getSize(),
                null, // urrl é obtica via presign endpoint; ou injete cache
                a.getCreatedAt()
        );
    }
}
