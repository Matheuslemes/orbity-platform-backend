package br.com.orbity.customer.application.usecase;

import br.com.orbity.customer.application.policy.TransactionalPolicy;
import br.com.orbity.customer.domain.event.AddressChangedEvent;
import br.com.orbity.customer.domain.model.Address;
import br.com.orbity.customer.domain.port.in.AddAddressCommand;
import br.com.orbity.customer.domain.port.in.RemoveAddressCommand;
import br.com.orbity.customer.domain.port.in.UpdateAddressCommand;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import br.com.orbity.customer.domain.port.out.OutboxPortOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageAddressUseCase implements AddAddressCommand, UpdateAddressCommand, RemoveAddressCommand {

    private final CustomerRepositoryPortOut repository;
    private final OutboxPortOut outbox;
    private final ObjectMapper om;
    private final TransactionalPolicy tx;

    @Override
    public void add(UUID customerId, String label, String street, String number, String complement,
                    String district, String city, String state, String country, String zip, boolean main) {

        log.info("[ManageAddressUseCase] - [add] IN -> customerId={}", customerId);
        tx.runInTx(() -> {

            var a = new Address(UUID.randomUUID(), trim(label), trim(street), trim(number), trim(complement),
                    trim(district), trim(city), trim(state), trim(country), trim(zip), main, OffsetDateTime.now(), OffsetDateTime.now());
            repository.addAddress(customerId, a);
            publish(customerId, a.id());

        });

    }

    @Override
    public void remove(UUID customerId, UUID addressId) {

        log.info("[ManageAddressUseCase] - [remove] IN -> customerId={} addressId={}", customerId, addressId);
        tx.runInTx(() -> {
            repository.removeAddress(customerId, addressId);
            publish(customerId, addressId);

        });

    }

    @Override
    public void update(UUID customerId, UUID addressId, String label, String street, String number, String complement,
                       String district, String city, String state, String country, String zip, boolean main) {

        log.info("[ManageAddressUseCase] - [update] IN -> customerId={} addressId={}", customerId, addressId);

        tx.runInTx(() -> {

            var a = new Address(addressId, trim(label), trim(street), trim(number), trim(complement),
                    trim(district), trim(city), trim(state), trim(country), trim(zip), main, null, OffsetDateTime.now());
            repository.updateAddress(customerId, a);
            publish(customerId, a.id());

        });

    }

    private void publish(UUID customerId, UUID addressId) {

        try {

            var evt = new AddressChangedEvent(customerId, addressId, OffsetDateTime.now());
            outbox.append(evt.getClass().getSimpleName(), om.writeValueAsString(evt),customerId);
        } catch (Exception e) {

            throw new IllegalStateException("serialize address event failed", e);
        }

    }

    private static String trim(String s){ return s==null?null:s.trim(); }

}
