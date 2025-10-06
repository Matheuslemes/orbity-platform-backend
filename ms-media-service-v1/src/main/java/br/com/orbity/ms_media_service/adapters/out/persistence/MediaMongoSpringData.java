package br.com.orbity.ms_media_service.adapters.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaMongoSpringData extends MongoRepository<MediaMongoDocument, String> {

}
