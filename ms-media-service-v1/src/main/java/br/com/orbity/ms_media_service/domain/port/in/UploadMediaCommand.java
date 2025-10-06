package br.com.orbity.ms_media_service.domain.port.in;

import br.com.orbity.ms_media_service.domain.model.MediaAsset;

public interface UploadMediaCommand {

    record Input(String filename, String contentType, byte[] bytes) {}

    MediaAsset upload(Input in);

}
