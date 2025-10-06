package br.com.orbity.ms_catalog_service_v1.mapping;

import br.com.orbity.ms_catalog_service_v1.adapters.out.persistence.ProductDocument;
import br.com.orbity.ms_catalog_service_v1.adapters.out.persistence.VariantDocument;
import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.model.Variant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
public class ProductMongoMapper {


    private static final ObjectMapper JSON = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new com.fasterxml.jackson.core.type.TypeReference<>() {};

    // domain -> document
    public ProductDocument toProductDocument(Product e) {

        if (e == null) return null;

        ProductDocument d = new ProductDocument();
        d.setId(e.id() != null ? e.id().toString() : null);
        d.setSku(e.sku());
        d.setName(e.name());
        d.setDescription(e.description());
        d.setSlug(e.slug());
        d.setVersion(e.version());
        d.setCreatedAt(toInstant(e.createdAt()));
        d.setUpdatedAt(toInstant(e.updatedAt()));

        List<VariantDocument> variants = new ArrayList<>();
        if (e.variants() != null) {
            for (Variant v : e.variants()) {
                variants.add(toVariantDocument(v));
            }
        }
        d.setVariants(variants);

        return d;
    }

    public VariantDocument toVariantDocument(Variant v) {

        if (v == null) return null;

        VariantDocument dv = new VariantDocument();
        dv.setId(v.id() != null ? v.id().toString() : null);
        dv.setSku(v.sku());
        dv.setName(v.name());
        dv.setAttributes(parseJsonToMapSafe(v.attributesJson()));
        return dv;
    }

    // document -> domain
    public Product toProductDomain(ProductDocument d) {

        if (d == null) return null;

        List<Variant> variants = new ArrayList<>();
        if (d.getVariants() != null) {
            for (VariantDocument dv : d.getVariants()) {
                variants.add(toVariantDomain(dv));
            }
        }

        return Product.restore(
                parseUuidSafe(d.getId()),
                d.getSku(),
                d.getName(),
                d.getDescription(),
                d.getSlug(),
                variants,
                toOffset(d.getCreatedAt()),
                toOffset(d.getUpdatedAt()),
                d.getVersion()
        );
    }

    public Variant toVariantDomain(VariantDocument dv) {

        if (dv == null) return null;
        return Variant.of(
                parseUuidSafe(dv.getId()),
                dv.getSku(),
                dv.getName(),
                writeMapToJsonSafe(dv.getAttributes())
        );
    }

    // helpers
    private Instant toInstant(OffsetDateTime odt) {

        return odt == null ? null : odt.toInstant();

    }

    private OffsetDateTime toOffset(Instant instant) {

        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);

    }

    private UUID parseUuidSafe(String id) {

        if (id == null || id.isBlank()) return null;

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Map<String, Object> parseJsonToMapSafe(String json) {
        if (json == null || json.isBlank()) return null;

        try {
            return JSON.readValue(json, MAP_TYPE);
        } catch (Exception ex) {
            // fallback: guarda como {"_raw: "...} para não perder dados
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("_raw", json);
            return m;
        }
    }

    private String writeMapToJsonSafe(Map<String, Object> map) {

        if (map == null) return null;

        try {
            return JSON.writeValueAsString(map);
        } catch (Exception ex) {
            return null;
        }
    }
}
