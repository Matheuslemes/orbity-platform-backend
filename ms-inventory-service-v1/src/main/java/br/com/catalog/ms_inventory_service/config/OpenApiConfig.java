package br.com.catalog.ms_inventory_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .version("v1")
                        .description(
                                "CQRS + Event Sourcing (light) para estoque: criação/ajuste de quantidades, " +
                                        "reserva/liberação e projeções para leitura. Eventos via Kafka, snapshots e read-model em PostgreSQL."
                        )
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")
                        )
                )
                .addTagsItem(new Tag().name("Stock").description("Operações de estoque (criação, atualização, ajuste)"))
                .addTagsItem(new Tag().name("Reservations").description("Reserva e liberação de estoque (saga/processos)"))
                .addTagsItem(new Tag().name("Health").description("Monitoramento e status da API"))
                // Segurança: JWT Bearer
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticação via token JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

}
