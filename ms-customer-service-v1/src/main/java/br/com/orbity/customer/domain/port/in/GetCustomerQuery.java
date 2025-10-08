package br.com.orbity.customer.domain.port.in;

import br.com.orbity.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface GetCustomerQuery {

    Optional<Customer> byId(UUID id);

    Optional<Customer> byEmail(String email);

    Optional<Customer> me(String subOrEmail);

}
