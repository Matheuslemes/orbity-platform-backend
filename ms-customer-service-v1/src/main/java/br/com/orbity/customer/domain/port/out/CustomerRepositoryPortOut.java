package br.com.orbity.customer.domain.port.out;

import br.com.orbity.customer.domain.model.Address;
import br.com.orbity.customer.domain.model.Consent;
import br.com.orbity.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPortOut {

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findBySub(String sub);

    Customer save(Customer customer);

    void addAddress(UUID customerId, Address a);

    void updateAddress(UUID customerId, Address a);

    void removeAddress(UUID customerId, UUID addressId);

    void updateConsent(UUID customerId, Consent c);

}
