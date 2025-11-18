package br.com.orbity.ms_media_service.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "media.storage", name = "type", havingValue = "azure")
public class AzureBlobConfig {

    @Bean
    public BlobServiceClient blobServiceClient(
            @Value("${media.storage.azure.connection-string:}") String connString,
            @Value("${media.storage.azure.account-name:}") String accountName
    ) {
        if (connString != null && !connString.isBlank()) {
            return new BlobServiceClientBuilder()
                    .connectionString(connString)
                    .buildClient();
        }

        var cred = new DefaultAzureCredentialBuilder().build();
        String endpoint = "https://" + accountName + ".blob.core.windows.net";
        return new BlobServiceClientBuilder()
                .endpoint(endpoint)
                .credential(cred)
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient(
            BlobServiceClient service,
            @Value("${media.storage.azure.container}") String container
    ) {
        BlobContainerClient client = service.getBlobContainerClient(container);
        if (!client.exists()) {
            client.create();
        }
        return client;
    }

}
