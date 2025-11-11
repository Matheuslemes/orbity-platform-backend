package br.com.orbity.ms_media_service.adapters.out.persistence;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Document(collection = "media")
@CompoundIndexes({
        // Se continuar também preenchendo container/blobName, mantenha este índice.
        @CompoundIndex(name = "uk_container_blob", def = "{'container': 1, 'blobName': 1}", unique = true)
})
public class MediaMongoDocument {

    @Id
    private UUID id;

    @Indexed
    private String fileName;

    private String contentType;

    private Long size;

    @Indexed
    private String checksumSha256;

    /** CHAVE ÚNICA CANÔNICA: ex. azure://orbity-assets/path/file.png */
    @Indexed(unique = true)
    private String storageKey;

    @Indexed
    private String storageProvider;

    @Indexed
    private String container;

    @Indexed
    private String blobName;

    private Map<String, String> metadata;

    @Indexed
    private Set<String> tags;

    @Indexed
    private String visibility;

    @Indexed
    private String status;

    @CreatedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    private OffsetDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Version
    private Long version;
}
