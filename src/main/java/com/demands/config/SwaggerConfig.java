package com.demands.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Swagger Demands API").version("1.0.0")
                .license(new License().name("Demanda API").url("http://localhost:8080/"))
                .description("This is a sample Demands API created using springdoc-openapi and OpenAPI 3.")
        );
    }
}
