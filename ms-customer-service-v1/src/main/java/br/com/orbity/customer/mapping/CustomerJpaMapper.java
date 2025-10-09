package br.com.orbity.customer.mapping;

import br.com.orbity.customer.adapters.out.persistence.CustomerJpaEntity;
import br.com.orbity.customer.adapters.out.persistence.CustomerSpringRepository;
import br.com.orbity.customer.domain.model.Address;
import br.com.orbity.customer.domain.model.Consent;
import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.model.CustomerId;

import java.util.ArrayList;

public class CustomerJpaMapper {

    public static Customer toDomain(CustomerJpaEntity e) {

        if (e == null) return null;

        var c = new Customer(CustomerId.of(e.getId()), e.getSub(), e.getEmail());
        c.updateProfile(e.getFirstName(), e.getLastName(), e.getPhone());
        if (e.getConsent() != null) {

            c.setConsent(new Consent(
                    e.getConsent().isMarketingOption(),
                    e.getConsent().isTermsAccepted(),
                    e.getConsent().isDataProcessing(),
                    e.getConsent().getUpdateAt()
            ));

        }

        c.setTimestamps(e.getCreatedAt(), e.getUpdatedAt());
        if (e.getAddresses() != null) {

            e.getAddresses().forEach(a -> c.addOrReplaceAddress(new Address(
                    a.getId(), a.getLabel(), a.getStreet(), a.getNumber(), a.getComplement(), a.getDistrict(),
                    a.getCity(), a.getState(), a.getCountry(), a.getZip(), a.isMain(), a.getCreatedAt(), a.getUpdatedAt()
            )));

        }

        return c;

    }

    public static void copyToEntity(Customer src, CustomerJpaEntity dts) {

        dts.setId(src.id().value());
        dts.setSub(src.sub());
        dts.setEmail(src.email());
        dts.setFirstName(src.firstName());
        dts.setLastName(src.lastName());
        dts.setPhone(src.phone());
        dts.setCreatedAt(src.createdAt());
        dts.setUpdatedAt(src.updatedAt());
        dts.setUpdatedAt(src.updatedAt());

        if (dts.getAddresses() ==  null) dts.setAddresses(new ArrayList<>());

    }
}
