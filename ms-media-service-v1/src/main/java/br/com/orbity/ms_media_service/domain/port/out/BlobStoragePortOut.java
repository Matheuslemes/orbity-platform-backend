package br.com.orbity.ms_media_service.domain.port.out;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

public interface BlobStoragePortOut {

    record Stored(String container, String blobName, long size, String contentType) {}

    Stored store(String blobName, byte[] data, String contentType, Map<String,String> metadata);

    URL presignedGet(String blobName, Duration ttl);

    void delete(String blobName);

}
