package br.com.orbity.customer.mapping;

import br.com.orbity.customer.domain.model.Address;
import br.com.orbity.customer.domain.model.Consent;
import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.model.CustomerId;
import br.com.orbity.customer.dto.AddressDto;
import br.com.orbity.customer.dto.ConsentDto;
import br.com.orbity.customer.dto.CustomerDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerDtoMapper {

    // Domain -> DTO
    public CustomerDto toDto(Customer c) {
        if (c == null) return null;

        var consent = c.consent() == null ? null :
                new ConsentDto(
                        c.consent().marketingOption(),
                        c.consent().termsAccepted(),
                        c.consent().dataProcessing(),
                        c.consent().updatedAt()
                );

        var addresses = c.addresses() == null ? List.<AddressDto>of() :
                c.addresses().stream().map(a ->
                        new AddressDto(
                                a.id(),
                                a.label(),
                                a.street(),
                                a.number(),
                                a.complement(),
                                a.district(),
                                a.city(),
                                a.state(),
                                a.country(),
                                a.zip(),
                                a.main(),
                                a.createdAt(),
                                a.updatedAt()
                        )
                ).toList();

        return new CustomerDto(
                c.id().value(),
                c.sub(),
                c.email(),
                c.firstName(),
                c.lastName(),
                c.phone(),
                addresses,
                consent,
                c.createdAt(),
                c.updatedAt()
        );
    }

    // DTO -> Domain
    public Address toDomain(AddressDto d) {
        if (d == null) return null;
        return new Address(
                d.id(),
                trim(d.label()),
                trim(d.street()),
                trim(d.number()),
                trim(d.complement()),
                trim(d.district()),
                trim(d.city()),
                trim(d.state()),
                trim(d.country()),
                trim(d.zip()),
                d.main() != null && d.main(),
                d.createdAt(),
                d.updatedAt()
        );
    }

    public Consent toDomain(ConsentDto d) {
        if (d == null) return null;
        return new Consent(
                d.marketingOption(),
                d.termsAccepted(),
                d.dataProcessing(),
                d.updatedAt()
        );
    }

    // pcional: se precisar converter CustomerDto -> Customer
    public Customer toDomain(CustomerDto d) {

        if (d == null) return null;

        var consent = toDomain(d.consent());
        var addresses = d.addresses() == null ? List.<Address>of()
                : d.addresses().stream().map(this::toDomain).toList();

        return new Customer(
                new CustomerId(d.id()),
                trim(d.sub()),
                trim(d.email()),
                trim(d.firstName()),
                trim(d.lastName()),
                trim(d.phone()),
                addresses,
                consent,
                d.createdAt(),
                d.updatedAt()
        );

    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}