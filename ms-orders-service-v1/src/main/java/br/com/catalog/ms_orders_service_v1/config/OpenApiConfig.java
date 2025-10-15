package br.com.catalog.ms_orders_service_v1.config;

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
    public OpenAPI ordersOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Orders Service API")
                        .version("v1")
                        .description("""
                                Serviço de Pedidos (Onion + Outbox):
                                - Persistência e leitura de pedidos e itens
                                - Outbox para publicação de eventos de status em Kafka
                                - PostgreSQL + Flyway; Redis (opcional) para cache de consultas/“Meus Pedidos”
                                """)

                        .contact(new Contact()
                                .name("Time Orbity")
                                .email("dev@orbity.example")
                        )
                )

                .addTagsItem(new Tag().name("Orders").description("Consulta de pedidos e detalhes"))
                .addTagsItem(new Tag().name("Order Items").description("Itens de pedido (uso interno/DTOs)"))
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
