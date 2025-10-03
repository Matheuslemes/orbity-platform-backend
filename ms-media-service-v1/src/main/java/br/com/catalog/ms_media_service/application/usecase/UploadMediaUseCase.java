package br.com.catalog.ms_media_service.application.usecase;

import br.com.catalog.ms_media_service.domain.model.MediaAsset;
import br.com.catalog.ms_media_service.domain.port.in.UploadMediaCommand;
import br.com.catalog.ms_media_service.domain.port.out.BlobStoragePortOut;
import br.com.catalog.ms_media_service.domain.port.out.MediaEventPublisherPortOut;
import br.com.catalog.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UploadMediaUseCase implements UploadMediaCommand {

    private final MediaRepositoryPortOut repository;
    private final BlobStoragePortOut storage;
    private final MediaEventPublisherPortOut publisher;

    public UploadMediaUseCase(MediaRepositoryPortOut repository, BlobStoragePortOut storage, MediaEventPublisherPortOut publisher) {
        this.repository = repository;
        this.storage = storage;
        this.publisher = publisher;
    }


    @Override
    public MediaAsset upload(Input in) {

        log.info("[UploadMediaUseCase] - [upload] IN -> filename={} ct={} bytes={}",
                in == null ? "null" : in.filename(), in ==null ? "null" : in.contentType(),
                in == null || in.bytes() == null ? 0 : in.bytes().length);

        if (in == null || in.bytes() == null || in.bytes().length == 0) {
            throw  new IllegalArgumentException("arquivo vazio");
        }

        if (StringUtils.isBlank(in.filename())) throw new IllegalArgumentException("filename obrigatório");
        if (StringUtils.isBlank(in.contentType())) throw new IllegalArgumentException("contentType obrigatório");

        UUID id = UUID.randomUUID();
        String objectKey = "media/%s/%s".formatted(id.toString(), in.filename().trim());

        storage.put(objectKey, in.bytes(), in.contentType());

        MediaAsset asset = MediaAsset.of(
                id, in.filename(), in.contentType(), in.bytes().length,
                null, objectKey, OffsetDateTime.now()
        );
        var saved = repository.save(asset);

        // evento simples
        publisher.publish(new MediaUploaded(saved.getId().toString(), saved.getFilename(), saved.getContentType()));

        log.info("[UploadMediaUseCase] - [upload] OUT -> id={} objectKey={}", saved.getId(), saved.getStorageKey());

        return saved;
    }

    // evento minimo para exemplo
    public record MediaUploaded(String id, String filename, String contentType) { }
}
