package br.com.catalog.ms_orders_service_v1.mapping;

import br.com.catalog.ms_orders_service_v1.adapters.out.persistence.OrderItemJpaEntity;
import br.com.catalog.ms_orders_service_v1.adapters.out.persistence.OrderJpaEntity;
import br.com.catalog.ms_orders_service_v1.domain.model.Money;
import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.model.OrderItem;
import br.com.catalog.ms_orders_service_v1.domain.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class OrderJpaMapper {

    public Order toDomain(OrderJpaEntity e) {

        if (e == null) return null;
        List<OrderItem> items = e.getItems() == null ? List.of() :
                e.getItems().stream().map(this::toDomainItem).toList();

        return new Order(
                e.getId(),
                e.getCustomerId(),
                Money.of(e.getTotalAmount(), e.getCurrency()),
                OrderStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                items
        );

    }

    public OrderItem toDomainItem(OrderItemJpaEntity i) {

        return new OrderItem(
                i.getId(),
                i.getProductId(),
                i.getSku(),
                i.getName(),
                i.getUnitPrice(),
                i.getQuantity(),
                i.getLineTotal()
        );

    }

    public OrderJpaEntity toEntity(Order d) {

        var e = new OrderJpaEntity();
        e.setId(d.id());
        e.setCustomerId(d.customerId());
        e.setTotalAmount(d.total().amount());
        e.setCurrency(d.status().name());
        e.setStatus(d.status().name());
        e.setCreatedAt(d.createdAt() != null ? d.createdAt() : OffsetDateTime.now());
        e.setUpdatedAt(d.updatedAt() != null ? d.updatedAt() : OffsetDateTime.now());

        return e;

    }

    public OrderItemJpaEntity toEntityItem(OrderItem i, OrderJpaEntity orderRef) {



    }
}
