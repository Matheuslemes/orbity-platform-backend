package br.com.orbity.customer.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class AddressJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerJpaEntity customer;

    private String label;

    private String street;

    private String number;

    private String complement;

    private String district;

    private String city;

    private String state;

    private String country;

    private String zip;

    private boolean main;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
