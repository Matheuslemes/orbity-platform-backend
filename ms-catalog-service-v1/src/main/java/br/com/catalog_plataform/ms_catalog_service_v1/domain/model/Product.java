package br.com.catalog_plataform.ms_catalog_service_v1.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class Product {

    private final UUID id;
    private String sku;
    private String name;
    private String description;
    private String slug;
    private final List<Variant> variants;

    private Price price;
    private Stock stock;
    private final List<Media> media;

    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Long version;

    private Product(UUID id,
                    String sku,
                    String name,
                    String description,
                    String slug,
                    List<Variant> variants,
                    Price price,
                    Stock stock,
                    List<Media> media,
                    OffsetDateTime createdAt,
                    OffsetDateTime updatedAt,
                    Long version) {
        this.id = (id != null ? id : UUID.randomUUID());
        this.sku = trimOrNull(sku);
        this.name = trimOrNull(name);
        this.description = trimOrNull(description);
        this.slug = trimOrNull(slug);
        this.variants = (variants != null ? new ArrayList<>(variants) : new ArrayList<>());
        this.price = price;
        this.stock = (stock != null ? stock : new Stock(0));
        this.media = (media != null ? new ArrayList<>(media) : new ArrayList<>());
        this.createdAt = (createdAt != null ? createdAt : OffsetDateTime.now());
        this.updatedAt = (updatedAt != null ? updatedAt : this.createdAt);
        this.version = version;

        validate();
    }

    public static Product of(UUID id,
                             String sku,
                             String name,
                             String description,
                             String slug,
                             List<Variant> variants) {
        return new Product(id,
                sku,
                name,
                description,
                slug, variants,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static Product restore(UUID id,
                                  String sku,
                                  String name,
                                  String description,
                                  String slug,
                                  List<Variant> variants,
                                  OffsetDateTime createdAt,
                                  OffsetDateTime updatedAt,
                                  Long version) {
        return new Product(id, sku, name, description, slug, variants, null, null, null, createdAt, updatedAt, version);
    }

    public static Product of(UUID id) {

        UUID pId = (id != null ? id : UUID.randomUUID());
        String fallbackSku = "SKU-" + pId.toString().substring(0, 8);
        String fallbackName = "Produto " + pId.toString().substring(0, 8);

        return new Product(pId,
                fallbackSku,
                fallbackName,
                null,
                null,
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public void updateBasics(String name, String description, String slug) {
        this.name = trimOrNull(name);
        this.description = trimOrNull(description);
        this.slug = trimOrNull(slug);
        touch();
        validate();
    }

    public void replaceVariants(List<Variant> newVariants) {
        this.variants.clear();
        if (newVariants != null) {
            this.variants.addAll(newVariants);
        }
        touch();
    }

    public void updateMaterializedPrice(String currency, double amount) {

        if (currency == null || currency.isBlank()) {
            throw  new IllegalArgumentException("currency é obrigatório");
        }

        BigDecimal value = BigDecimal.valueOf(amount);
        if (value.scale() < 2) value = value.setScale(2); // padroniza escala minima

        this.price = new Price(currency.trim().toUpperCase(Locale.ROOT), value);
        touch();

    }

    public void updateInternPrice(String curerncy, BigDecimal amount) {

        if (curerncy == null || curerncy.isBlank()) {
            throw new IllegalArgumentException("currency é obrigatório");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount é obrigatório");
        }

        this.price = new Price(curerncy.trim().toUpperCase(Locale.ROOT), amount);
        touch();

    }

    public void updateMaterializedStock(int available) {

        if (available < 0) available = 0;

        this.stock = new Stock(available);
        touch();

    }

    public void attachMedia(String url, String label) {

        String u = trimOrNull(url);

        for (Media m : media) {
            if (u.equalsIgnoreCase(m.url())) {
                if (!Objects.equals(trimOrNull(label), m.label())) {
                    media.remove(m);
                    media.add(new Media(u, trimOrNull(label), m.primary()));
                    touch();
                }
                return;

            }
        }

        media.add(new Media(u, trimOrNull(label), false));
        touch();

    }

    public void markPrimaryMedia(String url) {

        String u = trimOrNull(url);

        if (u == null) throw new IllegalArgumentException("url é obrigatória");
        boolean found = false;

        for (int i = 0; i < media.size(); i++) {
            Media m = media.get(i);
            boolean isTarget = u.equalsIgnoreCase(m.url());
            Media updated = new Media(m.url(), m.label(), isTarget);
            if (isTarget) found = true;
            media.set(i, updated);
        }

        if (!found) throw new NoSuchElementException("Mídia não encontrada: " + url);
        touch();

    }

    private void touch() {

        this.updatedAt = OffsetDateTime.now();

    }

    private void validate() {

        if (this.sku == null || this.sku.isBlank()) {
            throw new IllegalArgumentException("SKU é obrigatório");
        }

        if (this.name == null || this.name.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public UUID id() { return id; }
    public String sku() { return sku; }
    public String name() { return name; }
    public String description() { return description; }
    public String slug() { return slug; }
    public List<Variant> variants() { return Collections.unmodifiableList(variants); }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }
    public Long version() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
