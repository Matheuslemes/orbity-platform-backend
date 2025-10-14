package br.com.catalog.ms_orders_service_v1.mapping;

import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.model.OrderItem;
import br.com.catalog.ms_orders_service_v1.dto.OrderDto;
import br.com.catalog.ms_orders_service_v1.dto.OrderItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDtoMapper {

    public OrderDto toDto(Order o) {

        if (o == null) return null;
        List<OrderItemDto> items = o.items().stream().map(this::toDto).toList();
        return new OrderDto(
                o.id(),
                o.customerId(),
                o.total().amount(),
                o.total().currency(),
                o.status(),
                items,
                o.createdAt(),
                o.updatedAt()
        );
    }

    public OrderItemDto toDto(OrderItem i) {

        return new OrderItemDto(
                i.id(), i.productId(), i.sku(), i.name(),
                i.unitPrice(), i.quantity(), i.lineTotal()
        );

    }
}
