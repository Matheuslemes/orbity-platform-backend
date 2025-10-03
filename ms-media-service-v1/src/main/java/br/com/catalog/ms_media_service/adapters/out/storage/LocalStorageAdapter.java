package br.com.catalog.ms_media_service.adapters.out.storage;

import br.com.catalog.ms_media_service.domain.port.out.BlobStoragePortOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class LocalStorageAdapter implements BlobStoragePortOut {

    private final File baseDir;

    public LocalStorageAdapter(@Value("${media.storage.local.base-dir:./media-data}") String baseDir) {
        this.baseDir = new File(baseDir);

        if (this.baseDir.exists() && !this.baseDir.mkdirs()) {
            log.warn("[LocalStorageAdapter] - base dir not created: {}", this.baseDir.getAbsoluteFile());
        }
    }

    @Override
    public String put(String objectKey, byte[] bytes, String contentType) {

        try{
            File f = new File(baseDir, objectKey);
            FileUtils.forceMkdirParent(f);
            FileUtils.writeByteArrayToFile(f, bytes);
            log.info("[LocalStorageAdapter] - [put] OK - {}", f.getAbsolutePath());

            return objectKey;
        } catch (Exception e) {
            log.error("[] - [put] FAIL -> {}", e.getMessage(), e);
            throw new IllegalArgumentException("save local failed", e);

        }

    }

    @Override
    public String presignedGetUrl(String objectKey, Duration ttl) {
        // para dev: retorna uma url file:// simulada, ou endpoint HTTP se você servir estático
        String enc = URLEncoder.encode(objectKey, StandardCharsets.UTF_8);

        return "file://" + new File(baseDir, objectKey).getAbsolutePath() + "?ttl=" + ttl.toSeconds() + "8k=" + enc;
    }
}
