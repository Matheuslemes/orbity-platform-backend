package br.com.orbity.ms_media_service.adapters.out.storage;

import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "media.storage", name = "type", havingValue = "minio")
public class MinioStorageAdapter implements BlobStoragePortOut {

    private final MinioClient client;
    private final String bucket;

    public MinioStorageAdapter(
            @Value("${media.storage.minio.endpoint}") String endpoint,
            @Value("${media.storage.minio.access-key}") String accessKey,
            @Value("${media.storage.minio.secret-key}") String secretKey,
            @Value("${media.storage.minio.bucket:media}") String bucket,
            @Value("${media.storage.minio.secure:false}") boolean secure
    ) {
        this.client = MinioClient.builder()
                .endpoint(endpoint, secure ? 443 : 9000, secure)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
    }

    @Override
    public Stored store(String blobName, byte[] data, String contentType, Map<String, String> metadata) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("dados vazios");
        }

        try (var in = new ByteArrayInputStream(data)) {

            var builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(blobName)
                    .contentType(contentType)
                    .stream(in, data.length, -1);

            if (metadata != null && !metadata.isEmpty()) {
                builder.userMetadata(metadata);
            }

            client.putObject(builder.build());

            log.info("[MinioStorageAdapter] - [store] OK -> {}/{}", bucket, blobName);

            return new Stored(bucket, blobName, data.length, contentType);
        } catch (Exception e) {
            log.error("[MinioStorageAdapter] - [store] FAIL -> {}/{} - {}", bucket, blobName, e.getMessage(), e);
            throw new IllegalStateException("minio store failed", e);
        }
    }

    @Override
    public URL presignedGet(String blobName, Duration ttl) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            ttl = Duration.ofMinutes(10);
        }

        long seconds = ttl.getSeconds();
        if (seconds > 604800L) {
            seconds = 604800L;
        }

        try {
            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(blobName)
                            .method(Method.GET)
                            .expiry((int) seconds)
                            .build()
            );

            log.debug("[MinioStorageAdapter] - [presignedGet] OK -> {}/{}", bucket, blobName);

            return new URL(url);
        } catch (Exception e) {
            log.error("[MinioStorageAdapter] - [presignedGet] FAIL -> {}/{} - {}", bucket, blobName, e.getMessage(), e);
            throw new IllegalStateException("minio presign failed", e);
        }
    }

    @Override
    public void delete(String blobName) {
        if (blobName == null || blobName.isBlank()) {
            throw new IllegalArgumentException("blobName obrigatório");
        }

        try {
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(blobName)
                            .build()
            );

            log.info("[MinioStorageAdapter] - [delete] OK -> {}/{}", bucket, blobName);
        } catch (Exception e) {
            log.error("[MinioStorageAdapter] - [delete] FAIL -> {}/{} - {}", bucket, blobName, e.getMessage(), e);
            throw new IllegalStateException("minio delete failed", e);
        }
    }
}
