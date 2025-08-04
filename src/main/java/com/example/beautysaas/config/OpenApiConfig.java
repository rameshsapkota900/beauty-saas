package com.example.beautysaas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Beauty Parlour SaaS API",
                version = "2.0.0",
                description = "Comprehensive API documentation for the Multi-Tenant Beauty Parlour SaaS Management System with advanced security and features",
                contact = @Contact(name = "Beauty SaaS Support", email = "support@beautysaas.com", url = "https://beautysaas.com/support"),
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT"),
                termsOfService = "https://beautysaas.com/terms"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.beautysaas.com", description = "Production Server"),
                @Server(url = "https://staging-api.beautysaas.com", description = "Staging Server")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT authentication token. Prepend with 'Bearer ' (e.g., 'Bearer YOUR_TOKEN')"
)
public class OpenApiConfig {
    // Configuration for OpenAPI 3.0 (Swagger UI)
    // Access at http://localhost:8080/swagger-ui/index.html
}
