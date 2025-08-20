package com.beautyparlour.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beauty Parlour Management System API")
                        .version("1.0.0")
                        .description("Multi-tenant SaaS backend for Beauty Parlour Management System. " +
                                "This API provides comprehensive functionality for managing beauty parlours, " +
                                "including services, courses, bookings, staff, and financial operations.")
                        .contact(new Contact()
                                .name("Beauty Parlour SaaS Team")
                                .email("support@beautyparlour.com")
                                .url("https://github.com/rameshsapkota900/beauty-saas"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.beautyparlour.com").description("Production server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token required for authenticated endpoints. " +
                                                "Format: Bearer <token>")));
    }
}
