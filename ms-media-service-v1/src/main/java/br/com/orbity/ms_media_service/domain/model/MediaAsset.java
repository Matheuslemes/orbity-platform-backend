package br.com.orbity.ms_media_service.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class MediaAsset {

    private final UUID id;
    private final String filename;
    private final String contentType;
    private final long size;
    private final String checksum; // ex; sha256
    private final String storageKey;
    private final OffsetDateTime createdAt;


    public MediaAsset(UUID id, String filename, String contentType, long size, String checksum, String storageKey, OffsetDateTime createdAt) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
        this.storageKey = storageKey;
        this.createdAt = createdAt;
    }

    public static MediaAsset of(UUID id, String filename, String contentType, long size, String checksum, String storageKey, OffsetDateTime createdAt) {

        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(filename, "filename");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(storageKey, "storageKey");

        if (size < 0) throw new IllegalArgumentException("size < 0");;
        return new MediaAsset(id, filename.trim(), contentType.trim(), size, checksum, storageKey.trim(), createdAt);
    }

    public UUID getId() {
        return id;
    }
    public String getFilename() {
        return filename;
    }
    public String getContentType() {
        return contentType;
    }
    public long getSize() {
        return size;
    }
    public String getChecksum() {
        return checksum;
    }
    public String getStorageKey() {
        return storageKey;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
