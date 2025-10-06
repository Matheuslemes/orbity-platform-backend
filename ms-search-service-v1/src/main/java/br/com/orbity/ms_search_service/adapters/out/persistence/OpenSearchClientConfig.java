package br.com.orbity.ms_search_service.adapters.out.persistence;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient; 
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

    @Bean(destroyMethod = "close")
    public RestClient lowLevelRestClient(
            @Value("${opensearch.host:localhost}") String host,
            @Value("${opensearch.port:9200}") int port,
            @Value("${opensearch.scheme:http}") String scheme
    ) {
        log.info("[OpenSearchClientConfig] building low-level client host={} port={} scheme={}", host, port, scheme);
        return RestClient.builder(new HttpHost(host, port, scheme)).build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient lowLevelRestClient) {
        var transport = new RestClientTransport(lowLevelRestClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }
}
