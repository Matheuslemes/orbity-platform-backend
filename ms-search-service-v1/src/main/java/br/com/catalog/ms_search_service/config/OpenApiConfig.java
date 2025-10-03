package br.com.catalog.ms_search_service.config;

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
    public OpenAPI searchOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Seach Service API")
                        .version("v1")
                        .title("Indexação e busca de produtos em OpenSearch.")
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")
                        )
                )
                .addTagsItem(new Tag().name("Search").description("Consulta por produtos"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
