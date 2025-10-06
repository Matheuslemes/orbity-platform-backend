package br.com.orbity.ms_catalog_service_v1.mapping;

import br.com.orbity.ms_catalog_service_v1.domain.model.Product;
import br.com.orbity.ms_catalog_service_v1.domain.model.Variant;
import br.com.orbity.ms_catalog_service_v1.dto.ProductDto;
import br.com.orbity.ms_catalog_service_v1.dto.VariantDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDtoMapper {

    // Product -> DTO
    public ProductDto toDto(Product e) {
        if (e == null) return null;
        return ProductDto.builder()
                .id(e.id())
                .sku(e.sku())
                .name(e.name())
                .description(e.description())
                .slug(e.slug())
                .variants(toVariantDtoList(e.variants()))
                .version(e.version())                // <-- aqui é version (Long), não variants
                .build();
    }

    // DTO -> Product (novo)
    public Product toDomain(ProductDto dto) {
        if (dto == null) return null;

        Product p = Product.of(
                dto.id(),
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.slug(),
                toVariantList(dto.variants())
        );
        // Se vier versão no DTO (ex.: em update otimista), preserve
        if (dto.version() != null) {
            p.setVersion(dto.version());
        }
        return p;
    }

    // Variant -> DTO
    public VariantDto toDto(Variant e) {
        if (e == null) return null;
        return VariantDto.builder()
                .id(e.id())
                .sku(e.sku())
                .name(e.name())
                .attributesJson(e.attributesJson())
                .build();
    }

    // DTO -> Variant
    public Variant toDomain(VariantDto dto) {
        if (dto == null) return null;
        return Variant.of(
                dto.id(),
                dto.sku(),
                dto.name(),
                dto.attributesJson()
        );
    }

    // ---- Helpers ----
    private List<VariantDto> toVariantDtoList(List<Variant> list) {
        if (list == null) return null;
        List<VariantDto> out = new ArrayList<>();
        for (Variant v : list) out.add(toDto(v));
        return out;
    }

    private List<Variant> toVariantList(List<VariantDto> list) {
        if (list == null) return null;
        List<Variant> out = new ArrayList<>();
        for (VariantDto v : list) out.add(toDomain(v));
        return out;
    }
}
