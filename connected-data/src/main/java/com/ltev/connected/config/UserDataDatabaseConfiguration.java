package com.ltev.connected.config;

import com.ltev.connected.repository.userData.UserDetailsRepository;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories(basePackageClasses = UserDetailsRepository.class,
        entityManagerFactoryRef = "userDataEntityManagerFactory", transactionManagerRef = "userDataTransactionManager")
public class UserDataDatabaseConfiguration {
    @Primary
    @Bean
    @ConfigurationProperties("spring.userdata.datasource")
    public DataSourceProperties userDataDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Primary
    @Bean
    public DataSource userDataDataSource(@Qualifier("userDataDataSourceProperties") DataSourceProperties userDataDataSourceProperties) {
        return userDataDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean userDataEntityManagerFactory(DataSource userDataDataSource,
                                                                               EntityManagerFactoryBuilder builder) {
        return builder.dataSource(userDataDataSource)
                .packages("com.ltev.connected.domain")
                .persistenceUnit("userData")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager userDataTransactionManager(
            LocalContainerEntityManagerFactoryBean userDataEntityManagerFactory) {
        return new JpaTransactionManager(userDataEntityManagerFactory.getObject());
    }
}