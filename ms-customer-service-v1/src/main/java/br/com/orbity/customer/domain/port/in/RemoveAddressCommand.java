package br.com.orbity.customer.domain.port.in;

import java.util.UUID;

public interface RemoveAddressCommand {

    void remove(UUID customerId, UUID addressId);

}
