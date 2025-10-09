package br.com.orbity.customer.adapters.out.idp;

import br.com.orbity.customer.domain.port.out.IdpDirectoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class KeycloakDirectoryAdapter implements IdpDirectoryPortOut {


    @Override
    public Optional<IdProfile> lookupBySubOrEmail(String subOrEmail) {

        log.info("[KeycloakDirectoryAdapter] lookup {}", subOrEmail);
        return Optional.empty();

    }
}
