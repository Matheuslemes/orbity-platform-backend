package br.com.orbity.ms_search_service.adapters.out.persistence;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenSearchClientConfig {

    @Bean
    public OpenSearchClient openSearchClient(
            @Value("${search.opensearch.hots:localhost}") String host,
            @Value("${search.opensearch.port:9200}") int port,
            @Value("${search.opensearch.scheme:http}") String scheme
    ) {

        log.info("[OpenSearchClientConfig] building client host={} port={} scheme={}", host, port, scheme);
        RestClient lowLevelClient = RestClient.builder(new HttpHost(host, port, scheme)).build();
        RestClientTransport transport = new RestClientTransport(lowLevelClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }
}
