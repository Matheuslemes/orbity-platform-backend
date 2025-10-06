package br.com.orbity.ms_media_service.domain.port.out;

import java.time.Duration;

public interface BlobStoragePortOut {

    String put(String objectKey, byte[] bytes, String contentType);

    String presignedGetUrl(String objectKey, Duration ttl);

}
