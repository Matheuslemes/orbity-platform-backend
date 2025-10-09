package br.com.orbity.customer.adapters.out.persistence;

import br.com.orbity.customer.domain.model.Address;
import br.com.orbity.customer.domain.model.Consent;
import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import br.com.orbity.customer.mapping.CustomerJpaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPortOut {

    private final CustomerSpringRepository repository;

    @Override
    public Optional<Customer> findById(UUID id) {

        return repository.findById(id).map(CustomerJpaMapper::toDomain);

    }

    @Override
    public Optional<Customer> findByEmail(String email) {

        return repository.findByEmail(email).map(CustomerJpaMapper::toDomain);

    }

    @Override
    public Optional<Customer> findBySub(String sub) {

        return repository.findBySub(sub).map(CustomerJpaMapper::toDomain);

    }

    @Override
    public Customer save(Customer customer) {

        var entity = repository.findById(customer.id().value()).orElseGet(CustomerJpaEntity::new);
        CustomerJpaMapper.copyToEntity(customer, entity);

        if (entity.getCreatedAt() == null) entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        var saved = repository.save(entity);
        return CustomerJpaMapper.toDomain(saved);

    }

    @Override
    public void addAddress(UUID customerId, Address a) {

        var e = repository.findById(customerId).orElseThrow();
        var ae = new AddressJpaEntity();
        ae.setId(a.id());
        ae.setCustomer(e);
        ae.setLabel(a.label());
        ae.setStreet(a.street());
        ae.setNumber(a.number());
        ae.setComplement(a.complement());
        ae.setDistrict(a.district());
        ae.setCity(a.city());
        ae.setState(a.state());
        ae.setCountry(a.country());
        ae.setMain(a.main());
        ae.setCreatedAt(OffsetDateTime.now());
        ae.setUpdatedAt(OffsetDateTime.now());
        e.getAddresses().add(ae);
        repository.save(e);

    }

    @Override
    public void updateAddress(UUID customerId, Address a) {

        var e = repository.findById(customerId).orElseThrow();
        var found = e.getAddresses().stream().filter(x -> x.getId().equals(a.id())).findFirst().orElseThrow();
        found.setLabel(a.label());
        found.setStreet(a.street());
        found.setNumber(a.number());
        found.setComplement(a.complement());
        found.setDistrict(a.district());
        found.setCity(a.city());
        found.setState(a.state());
        found.setCountry(a.country());
        found.setZip(a.zip());
        found.setMain(a.main());
        found.setUpdatedAt(OffsetDateTime.now());
        repository.save(e);


    }

    @Override
    public void removeAddress(UUID customerId, UUID addressId) {

        var e = repository.findById(customerId).orElseThrow();
        e.getAddresses().removeIf(x -> x.getId().equals(addressId));
        repository.save(e);

    }

    @Override
    public void updateConsent(UUID customerId, Consent c) {

        var e = repository.findById(customerId).orElseThrow();
        var ce = e.getConsent();
        if (ce == null) {
            ce = new ConsentJpaEntity();
            ce.setCustomer(e);
            ce.setCustomerId(e.getId());
        }

        ce.setMarketingOption(c.marketingOption());
        ce.setTermsAccepted(c.termsAccepted());
        ce.setDataProcessing(c.dataProcessing());
        ce.setUpdateAt(OffsetDateTime.now());
        e.setConsent(ce);
        repository.save(e);

    }
}
