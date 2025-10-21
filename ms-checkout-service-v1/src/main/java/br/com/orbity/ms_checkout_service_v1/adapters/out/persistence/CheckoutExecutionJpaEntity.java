package br.com.orbity.ms_checkout_service_v1.adapters.out.persistence;

import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "checkout_execution")
@Getter @Setter
public class CheckoutExecutionJpaEntity {

    @Id
    private UUID id;

    private UUID customerId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private CheckoutStatus status;

    private String sagaStep;

    private String sagaCompensation;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    // Campos mínimos para reconstrução (itens, address, payment) podem ser serializados como JSON string
    @Lob
    private String payload; // JSON dos itens/endereço/pagamento (opcional, simples)

}
