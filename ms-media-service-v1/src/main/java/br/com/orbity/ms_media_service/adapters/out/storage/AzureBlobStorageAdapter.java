package br.com.orbity.ms_media_service.adapters.out.storage;

import br.com.orbity.ms_media_service.domain.port.out.BlobStoragePortOut;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "media.storage", name = "type", havingValue = "azure")
public class AzureBlobStorageAdapter implements BlobStoragePortOut {

    private final BlobContainerClient container;

    @Value("${media.storage.azure.sas-default-ttl:PT5M}")
    private Duration defaultTtl;

    @Override
    public Stored store(String blobName, byte[] data, String contentType, Map<String, String> metadata) {
        BlobClient blob = container.getBlobClient(blobName);

        blob.upload(new java.io.ByteArrayInputStream(data), data.length, true);
        blob.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
        if (metadata != null && !metadata.isEmpty()) {
            blob.setMetadata(metadata);
        }

        return new Stored(blob.getContainerName(), blob.getBlobName(), data.length, contentType);
    }

    @Override
    public URL presignedGet(String blobName, Duration ttl) {
        BlobClient blob = container.getBlobClient(blobName);

        BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);
        Duration effectiveTtl = (ttl != null ? ttl : defaultTtl);
        OffsetDateTime expiry = OffsetDateTime.now().plusSeconds(effectiveTtl.getSeconds());

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiry, perm);

        String sas = blob.generateSas(values);

        try {
            return new URL(blob.getBlobUrl() + "?" + sas);
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Erro gerando URL SAS", e);
        }
    }

    @Override
    public void delete(String blobName) {
        container.getBlobClient(blobName).delete();
    }
}
