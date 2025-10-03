package br.com.catalog.ms_inventory_service.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_read")
@Getter @Setter
public class StockReadJpaEntity {

    @Id
    private String sku;

    @Column(name = "available_qty", nullable = false)
    private Long availableQty;

    @Column(name = "reserved_qty", nullable = false)
    private Long reservedQty;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}