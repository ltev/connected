package com.ltev.connected.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ConnectedDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("spring.connected.datasource")
    public DataSourceProperties connectedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource connectedDataSource(DataSourceProperties connectedDataSourceProperties) {
        return connectedDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}