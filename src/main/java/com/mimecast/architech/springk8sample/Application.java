package com.mimecast.architech.springk8sample;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Mimecast Spring Boot Template"
        )
)
@SecurityScheme(name = "mimecast", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
