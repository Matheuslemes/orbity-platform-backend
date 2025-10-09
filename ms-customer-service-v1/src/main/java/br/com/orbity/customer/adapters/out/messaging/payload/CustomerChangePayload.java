package br.com.orbity.customer.adapters.out.messaging.payload;

import br.com.orbity.customer.domain.model.Customer;

public record CustomerChangePayload(

        String id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String eventType

) {
    public static CustomerChangePayload from(Customer c, String eventType) {

        return new CustomerChangePayload(

                c.id().toString(),
                c.email(),
                c.firstName(),
                c.lastName(),
                c.phone(),
                eventType

        );
    }
}
