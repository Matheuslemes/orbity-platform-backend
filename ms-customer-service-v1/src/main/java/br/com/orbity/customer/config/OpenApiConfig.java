package br.com.orbity.customer.config;

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
    public OpenAPI customerOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Customer Service API")
                        .version("v1")
                        .description("""
                                Serviço de Clientes (Hexagonal + Outbox):
                                - CRUD de perfil, endereços e consentimentos
                                - Outbox para publicação de eventos em Kafka
                                - PostgreSQL + Flyway; Redis (opcional) para cache de perfil
                                """)
                        .contact(new Contact()
                                .name("Time Orbity")
                                .email("dev@orbity.example")
                        )
                )
                .addTagsItem(new Tag().name("Customers").description("Perfil do cliente e leitura por ID/Token"))
                .addTagsItem(new Tag().name("Addresses").description("Gerenciamento de endereços"))
                .addTagsItem(new Tag().name("Consent").description("Atualização de consentimentos"))
                .addTagsItem(new Tag().name("Health").description("Status e monitoramento"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticação via JWT Bearer")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

}