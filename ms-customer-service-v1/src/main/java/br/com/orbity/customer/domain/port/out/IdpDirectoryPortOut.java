package br.com.orbity.customer.domain.port.out;

import java.util.Optional;

public interface IdpDirectoryPortOut {

    record IdProfile(String sub, String email, String firstName, String lasName) {}

    Optional<IdProfile> lookupBySubOrEmail(String subOrEmail);

}
