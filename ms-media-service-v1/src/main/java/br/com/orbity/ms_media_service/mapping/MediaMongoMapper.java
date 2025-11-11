package br.com.orbity.ms_media_service.mapping;

import br.com.orbity.ms_media_service.adapters.out.persistence.MediaMongoDocument;
import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import org.springframework.stereotype.Component;

@Component
public class MediaMongoMapper {

    /** Domain -> Document */
    public MediaMongoDocument toDocument(MediaAsset a) {
        var d = new MediaMongoDocument();
        d.setId(a.getId());
        d.setFileName(a.getFilename());
        d.setContentType(a.getContentType());
        d.setSize(a.getSize());                 // garantir preenchimento
        d.setChecksumSha256(a.getChecksum());         // pode ser sha256 em hex
        d.setStorageKey(a.getStorageKey());     // formato <provider>://<container>/<blob>
        d.setCreatedAt(a.getCreatedAt());
        return d;
    }

    public MediaAsset toDomain(MediaMongoDocument d) {
        return MediaAsset.of(
                d.getId(),
                d.getFileName(),
                d.getContentType(),
                d.getSize() == null ? 0L : d.getSize(),
                d.getChecksumSha256(),
                d.getStorageKey(),
                d.getCreatedAt()
        );
    }
}


