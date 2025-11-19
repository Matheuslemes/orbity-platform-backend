package br.com.orbity.ms_media_service.domain.port.in;

import java.util.UUID;

public interface DeleteMediaCommand {

    boolean delete(UUID id);

}
