package br.com.orbity.ms_catalog_service_v1.adapters.out.persistence;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document("products")
public class ProductDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String sku;

    private String name;
    private String description;

    @Indexed(unique = true)
    private String slug;

    @DocumentReference
    private List<VariantDocument> variants = new ArrayList<>();

    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
