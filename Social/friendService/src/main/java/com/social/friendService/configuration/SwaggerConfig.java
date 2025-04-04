package com.social.friendService.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Friend Service API",
                version = "1.0.0",
                description = "API documentation for Friend Service",
                contact = @Contact(name = "Your Name", email = "your.email@example.com")
        )
)
public class SwaggerConfig {
}
