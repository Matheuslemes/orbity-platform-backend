package br.com.orbity.ms_inventory_service.config;

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

    // Modo protegido: habilita Resource Server (JWT) quando issuer-uri ou jwk-set-uri estiverem configurados
    @Bean
    @Order(1)
    @ConditionalOnProperty(
            prefix = "spring.security.oauth2.resourceserver.jwt",
            name = {"issuer-uri", "jwk-set-uri"},
            matchIfMissing = false
    )
    public SecurityFilterChain oauth2ResourceServer(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // docs e actuator livres
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/actuator/health", "/actuator/info"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
        return http.build();
    }

    // Modo dev: tudo liberado quando não há configuração de JWT
    @Bean
    @Order(2)
    @ConditionalOnProperty(
            prefix = "spring.security.oauth2.resourceserver.jwt",
            name = {"issuer-uri", "jwk-set-uri"},
            matchIfMissing = true,
            havingValue = "" // também cai aqui se vier vazio
    )
    public SecurityFilterChain openDev(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();

    }

}
