package br.com.orbity.customer.application.usecase;

import br.com.orbity.customer.application.policy.TransactionalPolicy;
import br.com.orbity.customer.domain.event.CustomerCreatedEvent;
import br.com.orbity.customer.domain.event.CustomerUpdatedEvent;
import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.model.CustomerId;
import br.com.orbity.customer.domain.port.in.UpsertCustomerCommand;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import br.com.orbity.customer.domain.port.out.OutboxPortOut;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertCustomerUseCase implements UpsertCustomerCommand {

    private final CustomerRepositoryPortOut repository;
    private final OutboxPortOut outbox;
    private final ObjectMapper om;
    private final TransactionalPolicy tx;

    @Override
    public Customer upsert(String sub, String email, String firstName, String lastName, String phone) {

        log.info("[UpsertCustomerUseCase] - [upsert] IN -> sub={} email={}", sub, email);

        return tx.runInTx(() -> {

            var existing = repository.findBySub(sub).or(() -> repository.findByEmail(email));
            Customer c;
            boolean created = false;

            if (existing.isEmpty()) {

                c = new Customer(CustomerId.of(UUID.randomUUID()), sub, email);
                c.updateProfile(firstName, lastName, phone);
                c = repository.save(c);
                created = true;

            } else {

                c = existing.get();
                c.updateProfile(firstName, lastName, phone);
                c = repository.save(c);

            }

            var evt = created
                    ? new CustomerCreatedEvent(c.id().value(), c.email(), OffsetDateTime.now())
                    : new CustomerUpdatedEvent(c.id().value(), c.email(), OffsetDateTime.now());

            try {

                outbox.append(evt.getClass().getSimpleName(), om.writeValueAsString(evt), c.id().value());
            } catch (JsonProcessingException e) {

                throw new RuntimeException(e);
            }

            log.info("[UpsertCustomerUseCase] - [upsert] OUT -> id={} created={}", c.id().value(), created);

            return c;

        });

    }

}
