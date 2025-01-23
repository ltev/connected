package com.ltev.connected.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    @Bean
    @ConfigurationProperties("spring.flyway.connected")
    public DataSourceProperties connectedFlywayDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(initMethod = "migrate")   // call migrate method after loading spring context
    public Flyway flywayConnected(@Qualifier("connectedFlywayDataSourceProperties")
                                      DataSourceProperties dataSourceProperties) {
        return Flyway.configure()
                .dataSource(dataSourceProperties.getUrl(),
                        dataSourceProperties.getUsername(),
                        dataSourceProperties.getPassword())
                .locations("classpath:/db/migration/connected")
                .load();
    }

    @Bean
    @ConfigurationProperties("spring.flyway.connecteduserdata")
    public DataSourceProperties connectedUserDataFlywayDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(initMethod = "migrate")   // call migrate method after loading spring context
    public Flyway flywayConnectedUserData(@Qualifier("connectedUserDataFlywayDataSourceProperties")
                                              DataSourceProperties dataSourceProperties) {
        return Flyway.configure()
                .dataSource(dataSourceProperties.getUrl(),
                        dataSourceProperties.getUsername(),
                        dataSourceProperties.getPassword())
                .locations("classpath:/db/migration/connected-user-data")
                .load();
    }
}