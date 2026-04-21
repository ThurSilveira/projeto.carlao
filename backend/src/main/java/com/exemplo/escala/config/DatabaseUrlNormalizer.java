package com.exemplo.escala.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * Normalizes DATABASE_URL from Supabase/Heroku format (postgresql://)
 * to JDBC format (jdbc:postgresql://) before the Spring context loads.
 */
public class DatabaseUrlNormalizer implements EnvironmentPostProcessor {

    private static final String DB_URL_KEY = "DATABASE_URL";
    private static final String POSTGRES_SCHEME = "postgresql://";
    private static final String JDBC_SCHEME = "jdbc:postgresql://";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String raw = environment.getProperty(DB_URL_KEY);
        if (raw != null && raw.startsWith(POSTGRES_SCHEME) && !raw.startsWith("jdbc:")) {
            String normalized = JDBC_SCHEME + raw.substring(POSTGRES_SCHEME.length());
            Map<String, Object> props = new java.util.HashMap<>();
            props.put(DB_URL_KEY, normalized);
            environment.getPropertySources().addFirst(new MapPropertySource("normalizedDatabaseUrl", props));
        }
    }
}
