package br.com.orbity.customer.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "consents")
@Getter
@Setter
public class ConsentJpaEntity {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_id")
    private CustomerJpaEntity customer;

    @Column(name = "marketing_option", nullable = false)
    private boolean marketingOption;

    @Column(name = "terms_accepted", nullable = false)
    private boolean termsAccepted;

    @Column(name = "data_processing", nullable = false)
    private boolean dataProcessing;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist @PreUpdate
    void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

}
