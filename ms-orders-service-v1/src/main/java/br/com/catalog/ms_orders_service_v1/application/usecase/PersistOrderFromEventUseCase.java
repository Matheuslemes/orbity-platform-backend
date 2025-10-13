package br.com.catalog.ms_orders_service_v1.application.usecase;

import br.com.catalog.ms_orders_service_v1.application.policy.TransactionalPolicy;
import br.com.catalog.ms_orders_service_v1.domain.model.Money;
import br.com.catalog.ms_orders_service_v1.domain.model.Order;
import br.com.catalog.ms_orders_service_v1.domain.model.OrderItem;
import br.com.catalog.ms_orders_service_v1.domain.model.OrderStatus;
import br.com.catalog.ms_orders_service_v1.domain.port.out.OrderRepositoryPortOut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistOrderFromEventUseCase {

    private final ObjectMapper om;
    private final OrderRepositoryPortOut repository;
    private final TransactionalPolicy tx;

    public void persistFrom(String json) {

        log.info("[PersistOrderFromEventUseCase] - [persistFrom] IN ->");
        tx.runInTx(() -> {
            try {
                JsonNode root = om.readTree(json);
                UUID id = UUID.fromString(root.path("orderId").asText());
                UUID customerId = UUID.fromString(root.path("customerId").asText());
                String currency = root.path("currency").asText("BRL");
                BigDecimal total = root.path("totalAmount").decimalValue();

                List<OrderItem> items = new ArrayList<>();
                for (JsonNode n : root.withArray("items")) {

                    items.add(new OrderItem(
                            UUID.fromString(n.path("id").asText()),
                            UUID.fromString(n.path("productId").asText()),
                            trim(n.path("sku").asText(null)),
                            trim(n.path("name").asText(null)),
                            n.path("unitPrice").decimalValue(),
                            n.path("quantity").asInt(),
                            n.path("lineTotal").decimalValue()

                    ));
                }

                Order order = new Order(
                        id, customerId,
                        Money.of(total, currency),
                        OrderStatus.valueOf(root.path("status").asText("CREATED")),
                        OffsetDateTime.now(), OffsetDateTime.now(),
                        items
                );

                repository.save(order);

            } catch (Exception e) {

                throw new IllegalStateException("persist order from event failed; " + e.getMessage(), e);

            }
        });
    }

    private static String trim(String s){ return s==null?null:s.trim(); }

}
