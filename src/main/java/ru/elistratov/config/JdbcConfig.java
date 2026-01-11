package ru.elistratov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcMappingContext JdbcMappingContext() {
        JdbcMappingContext jdbcMappingContext = new JdbcMappingContext();
        jdbcMappingContext.setSingleQueryLoadingEnabled(true);
        return jdbcMappingContext;
    }
}
