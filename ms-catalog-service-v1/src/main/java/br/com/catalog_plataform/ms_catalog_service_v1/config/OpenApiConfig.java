package br.com.catalog_plataform.ms_catalog_service_v1.config;

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
    public OpenAPI catalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog Service API")
                        .version("v1")
                        .description("CRUD de produtos e variantes com integração Kafka para eventos de catálogo.")
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")

                ))
                .addTagsItem(new Tag().name("Products").description("Gerenciamento de produtos"))
                .addTagsItem(new Tag().name("Variants").description("Gerenciamento de variantes de produtos"))
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
