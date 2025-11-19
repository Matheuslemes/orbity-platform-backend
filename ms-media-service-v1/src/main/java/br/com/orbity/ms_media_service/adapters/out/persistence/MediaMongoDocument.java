package br.com.orbity.ms_media_service.adapters.out.persistence;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Document(collection = "media")
public class MediaMongoDocument {

    @Id
    private UUID id;

    @Indexed
    private String fileName;

    private String contentType;

    private Long size;

    @Indexed
    private String checksumSha256;

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
    private Instant createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Instant updatedAt;

    @Version
    private Long version;
}
