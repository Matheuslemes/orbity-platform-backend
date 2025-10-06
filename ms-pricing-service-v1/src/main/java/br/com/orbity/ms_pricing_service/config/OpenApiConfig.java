package br.com.orbity.ms_pricing_service.config;

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
    public OpenAPI pricingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pricing Service API")
                        .description("API para gestão de preços (histórico, vigência e eventos Kafka).")
                        .version("v1")
                        .description("Gestão de preços com histórico de vigência e publicação de eventos no Kafka.")
                        .contact(new Contact()
                                .name("Matheus Silva Lemes")
                                .email("matheuslemesmsl@gmail.com")
                        )
                )
                .addTagsItem(new Tag().name("Prices").description("Gerenciamento de preços e histórico"))
                .addTagsItem(new Tag().name("Health").description("Monitoramento e status "))

                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Autenticação via token JWT")))
                        .addSecurityItem(new SecurityRequirement().addList("bearereAuth"));
    }
}
