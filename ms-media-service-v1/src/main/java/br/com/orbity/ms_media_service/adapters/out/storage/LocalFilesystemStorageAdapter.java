package br.com.orbity.ms_media_service.adapters.out.storage;

import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "media.storage",
        name = "type",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalFilesystemStorageAdapter implements BlobStoragePortOut {

    private final Path basePath;
    private final String publicBaseUrl;

    public LocalFilesystemStorageAdapter(
            @Value("${media.storage.local.base-path:./.media}") String basePath,
            @Value("${media.storage.local.public-base-url:http://localhost:8083/files}") String publicBaseUrl
    ) {
        this.basePath = Path.of(basePath).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        log.info("[LocalFilesystemStorageAdapter] basePath={} publicBaseUrl={}", this.basePath, this.publicBaseUrl);
    }

    @Override
    public Stored store(String blobName, byte[] data, String contentType, Map<String, String> metadata) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("dados vazios");
        }

        try {
            Path target = basePath.resolve(blobName).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, data);

            log.info("[LocalFilesystemStorageAdapter] - [store] OK -> {}", target);
            return new Stored(
                    basePath.toString(),
                    blobName,
                    data.length,
                    contentType
            );
        } catch (IOException e) {
            log.error("[LocalFilesystemStorageAdapter] - [store] FAIL -> blobName={} msg={}", blobName, e.getMessage(), e);
            throw new IllegalStateException("local store failed", e);
        }
    }

    @Override
    public URL presignedGet(String blobName, Duration ttl) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }

        String urlStr = publicBaseUrl + "/" + blobName;
        try {
            URL url = new URL(urlStr);
            log.debug("[LocalFilesystemStorageAdapter] - [presignedGet] -> {}", url);
            return url;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL inválida para blobName=" + blobName, e);
        }
    }

    @Override
    public void delete(String blobName) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }

        Path target = basePath.resolve(blobName).normalize();
        try {
            Files.deleteIfExists(target);
            log.info("[LocalFilesystemStorageAdapter] - [delete] OK -> {}", target);
        } catch (IOException e) {
            log.error("[LocalFilesystemStorageAdapter] - [delete] FAIL -> {} msg={}", target, e.getMessage(), e);
            throw new IllegalStateException("local delete failed", e);
        }
    }
}
