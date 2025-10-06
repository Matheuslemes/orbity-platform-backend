package br.com.orbity.ms_catalog_service_v1.adapters.out.persistence;

import lombok.Data;
import org.apache.kafka.clients.admin.ClientMetricsResourceListing;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document("variants")
public class VariantDocument {

    private String id; //

    private String sku;
    private String name;

    private Map<String, Object> attributes;
}
