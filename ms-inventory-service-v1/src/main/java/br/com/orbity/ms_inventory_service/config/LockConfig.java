package br.com.orbity.ms_inventory_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LockConfig {

    @Bean
    public Duration lockTtl() {

        return Duration.ofSeconds(15);

    }
}
