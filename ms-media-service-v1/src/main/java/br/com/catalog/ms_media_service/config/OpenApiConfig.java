package br.com.catalog.ms_media_service.config;

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
    public OpenAPI mediaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Media Service API")
                        .version("v1")
                        .description("Upload/consulta de mídias (HEX) com Mongo + Blob + Redis."
                        )
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")
                        )
                )
                .addTagsItem(new Tag().name("Media").description("Gerenciamento de assets de mídia"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
