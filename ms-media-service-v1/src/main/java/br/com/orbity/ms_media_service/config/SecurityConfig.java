package br.com.orbity.ms_media_service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    @ConditionalOnProperty(
            prefix = "spring.security.oauth2.resourceserver.jwt",
            name = {"issuer-uri", "jwk-set-uri"},
            matchIfMissing = false
    )
    public SecurityFilterChain secured(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui.html", "/swaggwe-ui", "/v3/api-docs/**",
                                "/actuator/health", "/actuator/info").permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(jwt -> {}));

        return http.build();
    }

    @Bean
    @Order(2)
    @ConditionalOnProperty(
            prefix = "spring.securyty.oauth2.resourceserver.jwt",
            name = {"issuer-uri", "jwt-set-uri"},
            matchIfMissing = true,
            havingValue = ""
    )
    public SecurityFilterChain openDev(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

}
