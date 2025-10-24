package br.com.orbity.ms_checkout_service_v1.application.policy;

import org.springframework.stereotype.Component;

@Component
public class IdempotencyPolicy {

    public String key(String prefix, String idempotencyKey){

        return prefix + ":" + (idempotencyKey == null ? "missing" : idempotencyKey.trim());

    }

}
