package br.com.orbity.ms_cart_service_v1.config;

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
    public OpenAPI cartOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cart Service API")
                        .version("v1")
                        .description("CRUD de carrinho (Redis como primary store, TTL por item/chave) com integração Kafka opcional.")
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")
                        ))
                .addTagsItem(new Tag().name("Cart").description("Gerenciamento do carrinho"))
                .addTagsItem(new Tag().name("Items").description("Gerenciamento de itens do carrinho"))
                .addTagsItem(new Tag().name("Health").description("Monitoramento e status da API"))

                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticação via token JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

}
