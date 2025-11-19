package br.com.orbity.ms_media_service.application.usecase;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;
import br.com.orbity.ms_media_service.domain.port.in.DeleteMediaCommand;
import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import br.com.orbity.ms_media_service.domain.port.out.MediaEventPublisherPortOut;
import br.com.orbity.ms_media_service.domain.port.out.MediaRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteMediaUseCase implements DeleteMediaCommand {

    private final MediaRepositoryPortOut repository;
    private final BlobStoragePortOut storage;
    private final MediaEventPublisherPortOut publisher;

    @Override
    public boolean delete(UUID id) {

        log.info("[DeleteMediaUseCase] - [delete] IN -> id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("id obrigatório");
        }

        var opt = repository.findById(id);

        if (opt.isEmpty()) {

            log.warn("[DeleteMediaUseCase] - [delete] media não encontrada -> id={}", id);

            return false;
        }

        MediaAsset asset = opt.get();

        try {
            storage.delete(asset.getStorageKey());
            log.info("[DeleteMediaUseCase] - [delete] storage OK -> key={}", asset.getStorageKey());
        } catch (Exception e) {

            log.error("[DeleteMediaUseCase] - [delete] erro ao deletar no storage -> key={} msg={}",
                    asset.getStorageKey(), e.getMessage(), e);

            throw new IllegalStateException("Falha ao deletar no storage", e);

        }

        repository.deleteById(id);

        publisher.publish(new MediaDeleted(asset.getId().toString(), asset.getFilename(), asset.getContentType()));
        log.info("[DeleteMediaUseCase] - [delete] OUT -> id={} deletado", id);

        return true;
    }

    public record MediaDeleted(String id, String filename, String contentType) { }

}