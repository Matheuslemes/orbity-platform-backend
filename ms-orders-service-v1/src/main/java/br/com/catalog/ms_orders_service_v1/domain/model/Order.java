package br.com.catalog.ms_orders_service_v1.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;

    private final UUID customerId;

    private Money total;

    private OrderStatus status;

    private final List<OrderItem> items = new ArrayList<>();

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    public Order(UUID id, UUID customerId, Money total, OrderStatus status,
                 OffsetDateTime createdAt, OffsetDateTime updatedAt, List<OrderItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.total = total;
        this.status = status;
        if (items != null) this.items.addAll(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID id(){ return id; }
    public UUID customerId(){ return customerId; }
    public Money total(){ return total; }
    public OrderStatus status(){ return status; }
    public List<OrderItem> items(){ return items; }
    public OffsetDateTime createdAt(){ return createdAt; }
    public OffsetDateTime updatedAt(){ return updatedAt; }

    public void updateStatus(OrderStatus s){ this.status = s; touch(); }
    public void touch(){ this.updatedAt = OffsetDateTime.now(); }
}
