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
    private UUID customerId;

    @OneToMany(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_id")
    private CustomerJpaEntity customer;

    private boolean marketingOption;

    private boolean termsAccepted;

    private boolean dataProcessing;

    private OffsetDateTime updateAt;

}
