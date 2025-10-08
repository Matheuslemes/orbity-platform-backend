package br.com.orbity.customer.application.usecase;

import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.port.in.GetCustomerQuery;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCustomerUseCase implements GetCustomerQuery {

    private final CustomerRepositoryPortOut repository;

    @Override
    public Optional<Customer> byId(UUID id) {

        log.info("[GetCustomerUseCase] - [byId] IN -> id={}", id);
        return repository.findById(id);

    }

    @Override
    public Optional<Customer> byEmail(String email) {

        log.info("[GetCustomerUseCase] - [byEmail] IN -> email={}", email);
        return repository.findByEmail(email);

    }

    @Override
    public Optional<Customer> me(String subOrEmail) {

        log.info("[GetCustomerUseCase] - [me] IN -> subOrEmail={}", subOrEmail);
        return repository.findBySub(subOrEmail).or(() -> repository.findByEmail(subOrEmail));

    }
}
