package br.com.orbity.ms_catalog_service_v1.adapters.out.cache;

import br.com.orbity.ms_catalog_service_v1.dto.ProductDto;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class RedisCatalogCacheAdapter {

    private final Cache productById;
    private final Cache productList;

    public RedisCatalogCacheAdapter(CacheManager cm) {
        this.productById = (Cache) cm.getCache("productById");
        this.productList = (Cache) cm.getCache("productList");
    }

    public ProductDto getProductById(String id) {
        return productById == null ? null : productById.get(id, ProductDto.class);
    }

    public void putProductById(String id, ProductDto dto) {
        if (productById != null) productById.put(id, dto);
    }

    public void evictProductById(String id) {
        if (productById != null) productById.evict(id);
    }

    public void evictProductList() {
        if (productList != null) productList.clear();
    }
}
