package br.com.orbity.customer.domain.port.in;

import java.util.UUID;

public interface UpdateAddressCommand {

    void update(UUID customerId, UUID addressId, String label, String street, String number, String complement,
                String district, String city, String state, String country, String zip, boolean main);

}
