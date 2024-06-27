package com.example.card.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Card API", version = "1.0",
        description = "API for getting card info",
        contact = @Contact(name = "Popescu Mihnea-Valentin"))
)
public class OpenApiConfiguration {
}
