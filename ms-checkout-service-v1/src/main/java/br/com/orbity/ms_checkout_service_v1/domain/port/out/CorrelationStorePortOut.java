package br.com.orbity.ms_checkout_service_v1.domain.port.out;

import java.util.Optional;

public interface CorrelationStorePortOut {

    boolean tryLock(String key, long ttSeconds);

    void release(String key);

    Optional<String> get(String key);

    void put(String key, String value, long ttlSeconds);

}
