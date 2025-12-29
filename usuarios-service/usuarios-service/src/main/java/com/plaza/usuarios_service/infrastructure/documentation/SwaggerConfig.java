package com.plaza.usuarios_service.infrastructure.documentation;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI usuariosServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Usuarios")
                        .description("API para gestión de usuarios y autenticación")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Key Ovallos")
                                .email("keylyjohanaod@ufps.edu.co")
                                .url("https://github.com/keyClosed")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("")
                        .url("https://github.com/keyClosed/Microservicios-Usuarios")
                );
    }
}