package ru.otus.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    // http://localhost:8080/swagger-ui/index.html
    // http://localhost:8080/swagger-ui

    @Bean
    public GroupedOpenApi publicApi(@Value("${rest.api.prefix}${rest.api.version}") String prefix) {
        return GroupedOpenApi.builder()
                .group("routesCatalogerApi")
                .pathsToMatch(String.format("%s/**", prefix))
                .build();
    }
}
