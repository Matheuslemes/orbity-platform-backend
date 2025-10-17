package br.com.orbity.ms_checkout_service_v1.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "catalog.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    //  vazio – usa auto-config do Spring Boot.

}