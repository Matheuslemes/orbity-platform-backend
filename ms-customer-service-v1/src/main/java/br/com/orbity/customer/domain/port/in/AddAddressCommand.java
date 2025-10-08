package br.com.orbity.customer.domain.port.in;

import java.util.UUID;

public interface AddAddressCommand {

    void add(UUID customerId, String label, String street, String number, String complement,
             String district, String city, String state, String country, String zip, boolean main);

}
