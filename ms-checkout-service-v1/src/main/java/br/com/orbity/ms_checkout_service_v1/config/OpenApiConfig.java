package br.com.orbity.ms_checkout_service_v1.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI checkoutOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Checkout Service API")
                        .version("v1")
                        .description("""
                                Serviço de Checkout
                                - SAGA Orchestrator (stateless com Redis ou stateful com Postgres)
                                - Outbox → Kafka
                                - Perfis: redis-saga / postgres-saga / secured
                                """)
                        .contact(new Contact().name("Time Catalog").email("dev@catalog.example"))
                )
                .addTagsItem(new Tag().name("Checkout").description("Início de checkout e status"))
                .addTagsItem(new Tag().name("Saga").description("Eventos de orquestração"))
                .addTagsItem(new Tag().name("Health").description("Status e monitoramento"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

}