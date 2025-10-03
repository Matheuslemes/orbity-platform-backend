package br.com.catalog.ms_media_service.adapters.out.storage;

import br.com.catalog.ms_media_service.domain.port.out.BlobStoragePortOut;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.Duration;

@Slf4j
@Component
@Profile("minio")
public class MinioStorageAdapter implements BlobStoragePortOut {

    private final MinioClient client;
    private final String bucket;

    public MinioStorageAdapter(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket:media}") String bucket
    ) {
        this.client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        this.bucket = bucket;
    }


    @Override
    public String put(String objectKey, byte[] bytes, String contentType) {

        try (var in = new ByteArrayInputStream(bytes)) {

            client.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .contentType(contentType)
                            .stream(in, bytes.length, - 1)
                    .build());

            log.info("[MinioStorageAdapter] - [put] OK -> {}/{}", bucket, objectKey);

            return objectKey;
        } catch (Exception e) {
            log.error("[MinioStorageAdapter] - [put] FAIL -> {}", e.getMessage(), e);
            throw new IllegalArgumentException("minio put failed", e);
        }

    }

    @Override
    public String presignedGetUrl(String objectKey, Duration ttl) {

        try {

            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .method(Method.GET)
                            .expiry((int) ttl.toSeconds())
                    .build());

        } catch (Exception e) {

            log.error("[MinioStorageAdapter] - [presignedGetUrl] FAIL -> {}", e.getMessage(), e);
            throw new IllegalArgumentException("minio presign failed", e);
        }

    }
}
