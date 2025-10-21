package br.com.orbity.ms_checkout_service_v1.mapping;

import br.com.orbity.ms_checkout_service_v1.adapters.out.persistence.CheckoutExecutionJpaEntity;
import br.com.orbity.ms_checkout_service_v1.domain.model.Checkout;
import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutItem;
import br.com.orbity.ms_checkout_service_v1.domain.model.SagaState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckoutJpaMapper {

    private final ObjectMapper om;

    public CheckoutExecutionJpaEntity toEntity(Checkout c){

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

            e.setPayload(om.writeValueAsString(c.items()));

        } catch (Exception ex) {

            throw new IllegalStateException("serialize items failed", ex);

        }

        return e;

    }

    public Checkout toDomain(CheckoutExecutionJpaEntity e){
        try {

            List<CheckoutItem> items = om.readValue(e.getPayload(), new TypeReference<>() {});

            return new Checkout(
                    e.getId(), e.getCustomerId(), items,
                    null, null,
                    e.getTotalAmount(), e.getStatus(),
                    new SagaState(e.getSagaStep(), e.getSagaCompensation(), OffsetDateTime.now()),
                    e.getCreatedAt(), e.getUpdatedAt()
            );

        } catch (Exception ex) {

            throw new IllegalStateException("deserialize items failed", ex);

        }

    }

}