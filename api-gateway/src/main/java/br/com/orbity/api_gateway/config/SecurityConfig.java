package br.com.orbity.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    @Order(1)
    SecurityWebFilterChain jwtSecurity(ServerHttpSecurity http,
                                       @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuer) {

        if (issuer != null && !issuer.isBlank()) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(reg -> reg
                            .pathMatchers("/actuator/**").permitAll()
                            .anyExchange().authenticated()
                    )
                    .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                    .build();
        }
        return null;
    }

    @Bean
    @Order(2)
    SecurityWebFilterChain openSecurity(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(reg -> reg.anyExchange().permitAll())
                .build();
    }
}