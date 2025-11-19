package br.com.orbity.ms_media_service.mapping;

import br.com.orbity.ms_media_service.adapters.out.persistence.MediaMongoDocument;
import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class MediaMongoMapper {

    public MediaMongoDocument toDocument(MediaAsset a) {
        var d = new MediaMongoDocument();
        d.setId(a.getId());
        d.setFileName(a.getFilename());
        d.setContentType(a.getContentType());
        d.setSize(a.getSize());
        d.setChecksumSha256(a.getChecksum());
        d.setStorageKey(a.getStorageKey());

        if (a.getCreatedAt() != null) {
            d.setCreatedAt(a.getCreatedAt().toInstant());   // 🔹 OffsetDateTime -> Instant
        }

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
                d.getCreatedAt() == null
                        ? null
                        : d.getCreatedAt().atOffset(ZoneOffset.UTC) // 🔹 Instant -> OffsetDateTime
        );
    }
}
