package br.com.orbity.customer.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerSpringRepository extends JpaRepository<CustomerJpaEntity, UUID> {

    Optional<CustomerJpaEntity> findByEmail(String email);

    Optional<CustomerJpaEntity> findBySub(String sub);

}
