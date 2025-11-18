package br.com.orbity.ms_media_service.application.usecase;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.domain.port.in.UploadMediaCommand;
import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import br.com.orbity.ms_media_service.domain.port.out.MediaEventPublisherPortOut;
import br.com.orbity.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadMediaUseCase implements UploadMediaCommand {

    private final MediaRepositoryPortOut repository;
    private final BlobStoragePortOut storage;
    private final MediaEventPublisherPortOut publisher;

    @Override
    public MediaAsset upload(Input in) {
        if (in == null) {
            throw new IllegalArgumentException("input obrigatório");
        }

        var bytes = in.bytes();
        String filename = StringUtils.trimToNull(in.filename());
        String contentType = StringUtils.trimToNull(in.contentType());

        log.info("[UploadMediaUseCase] - [upload] IN -> filename={} ct={} bytes={}",
                filename,
                contentType,
                bytes == null ? 0 : bytes.length
        );

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("arquivo vazio");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename obrigatório");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("contentType obrigatório");
        }

        String safeFilename = extractFileName(filename);

        UUID id = UUID.randomUUID();
        String blobName = "media/%s/%s".formatted(id, safeFilename);

        BlobStoragePortOut.Stored stored = storage.store(
                blobName,
                bytes,
                contentType,
                Map.of()
        );

        MediaAsset asset = MediaAsset.of(
                id,
                safeFilename,
                stored.contentType() != null ? stored.contentType() : contentType,
                stored.size(),
                null,
                stored.blobName(),
                OffsetDateTime.now()
        );

        var saved = repository.save(asset);

        publisher.publish(new MediaUploaded(
                saved.getId().toString(),
                saved.getFilename(),
                saved.getContentType()
        ));

        log.info("[UploadMediaUseCase] - [upload] OUT -> id={} blobName={}",
                saved.getId(), saved.getStorageKey());

        return saved;
    }

    public record MediaUploaded(String id, String filename, String contentType) { }

    private String extractFileName(String filename) {
        if (filename == null) {
            return null;
        }
        String normalized = filename.replace("\\", "/");
        int idx = normalized.lastIndexOf('/');
        return (idx >= 0 ? normalized.substring(idx + 1) : normalized).trim();
    }
}
