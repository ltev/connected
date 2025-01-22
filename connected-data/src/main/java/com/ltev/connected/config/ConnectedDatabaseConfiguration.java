package com.ltev.connected.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.ltev.connected.repository.main",
        entityManagerFactoryRef = "connectedEntityManagerFactory", transactionManagerRef = "connectedTransactionManager")
public class ConnectedDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("spring.connected.datasource")
    public DataSourceProperties connectedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource connectedDataSource(
            @Qualifier("connectedDataSourceProperties") DataSourceProperties connectedDataSourceProperties) {

        return connectedDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean//("entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean connectedEntityManagerFactory(
            @Qualifier("connectedDataSource") DataSource connectedDataSource,
            EntityManagerFactoryBuilder builder) {

        return builder.dataSource(connectedDataSource)
                .packages("com.ltev.connected.domain")
                .persistenceUnit("connected")
                .build();
    }

    @Bean//("transactionManager")
    @Primary
    public PlatformTransactionManager connectedTransactionManager(
            @Qualifier("connectedEntityManagerFactory") LocalContainerEntityManagerFactoryBean connectedEntityManagerFactoryBean) {

        return new JpaTransactionManager(connectedEntityManagerFactoryBean.getObject());
    }

    @Bean
    @Primary
    public JdbcTemplate connectedDataJdbcTemplate(@Qualifier("connectedDataSource") DataSource userDataDataSource) {
        return new JdbcTemplate(userDataDataSource);
    }
}