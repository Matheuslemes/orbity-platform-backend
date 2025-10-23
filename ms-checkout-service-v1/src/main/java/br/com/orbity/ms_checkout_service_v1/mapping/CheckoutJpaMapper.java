package br.com.orbity.ms_checkout_service_v1.mapping;

import br.com.orbity.ms_checkout_service_v1.adapters.out.persistence.CheckoutExecutionJpaEntity;
import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutItem;
import br.com.orbity.ms_checkout_service_v1.domain.model.SagaState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckoutJpaMapper {

    private final ObjectMapper om;

    public CheckoutExecutionJpaEntity toEntity(Checkout c) {
        var e = new CheckoutExecutionJpaEntity();

        e.setId(c.id());
        e.setCustomerId(c.customerId());
        e.setTotalAmount(c.totalAmount());
        e.setStatus(c.status());
        e.setSagaStep(c.saga() == null ? null : c.saga().getStep());
        e.setSagaCompensation(c.saga() == null ? null : c.saga().getCompensationStep());

        e.setCreatedAt(c.createdAt());
        e.setUpdatedAt(c.updatedAt());

        try {
            JsonNode itemsJson = om.valueToTree(c.items()); // -> JsonNode
            e.setPayload(itemsJson);
        } catch (Exception ex) {
            throw new IllegalStateException("serialize items to JSON failed", ex);
        }

        return e;

    }

    public Checkout toDomain(CheckoutExecutionJpaEntity e) {

        try {

            List<CheckoutItem> items;

            JsonNode payload = e.getPayload();

            if (payload == null || payload.isNull() || (payload.isArray() && payload.isEmpty())) {

                items = Collections.emptyList();

            } else {

                items = om.convertValue(payload, new TypeReference<List<CheckoutItem>>() {});

            }

            return new Checkout(

                    e.getId(),
                    e.getCustomerId(),
                    items,
                    null,
                    null,
                    e.getTotalAmount(),
                    e.getStatus(),
                    new SagaState(
                            e.getSagaStep(),
                            e.getSagaCompensation(),
                            e.getUpdatedAt() != null ? e.getUpdatedAt() : OffsetDateTime.now()
                    ),
                    e.getCreatedAt(),
                    e.getUpdatedAt()
            );

        } catch (Exception ex) {

            throw new IllegalStateException("deserialize items from JSON failed", ex);

        }

    }

}
