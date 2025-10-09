package br.com.orbity.customer.mapping;

import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.dto.AddressDto;
import br.com.orbity.customer.dto.ConsentDto;
import br.com.orbity.customer.dto.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerDtoMapper {

    public CustomerDto toDto(Customer c) {

        if (c == null) return null;
        var consent = c.consent() == null ? null :
                new ConsentDto(
                        c.consent().marketingOption(),
                        c.consent().termsAccepted(),
                        c.consent().dataProcessing(),
                        c.consent().updateAt());

        var addresses = c.addresses().stream().map(a ->
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
                        a.updatedAt())
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
}
