package br.com.orbity.customer.domain.port.in;

import br.com.orbity.customer.domain.model.Customer;

public interface UpsertCustomerCommand {

    Customer upsert(String sub, String email, String firstName, String lastName, String phone);

}
