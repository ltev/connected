package com.ltev.connected.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    public static final String LOGIN_FORM_URL = "/login";
    public static final String AUTHENTICATE_USER_URL = "/authenticate-user";
    public static final String ACCESS_DENIED_URL = "/access-denied";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/profile/**").permitAll()
                                .requestMatchers("/post/**").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage(LOGIN_FORM_URL)
                        // no controller request mapping required - will be taken care by spring security filters
                        .loginProcessingUrl(AUTHENTICATE_USER_URL)
                        .permitAll()
                )
                // add logout support
                .logout(logout -> logout.permitAll())
                    .exceptionHandling(configurer -> configurer
                    .accessDeniedPage(ACCESS_DENIED_URL));
        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        String users_roles_view = """
                select u.username, r.name as role
                    FROM users u
                    LEFT JOIN users_roles ur ON u.id = ur.user_id
                    LEFT JOIN roles r ON ur.role_id = r.id
                    where u.username = ?""";

        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

        userDetailsManager.setUsersByUsernameQuery(
                "select username, password, enabled from users where username = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery(users_roles_view);

        userDetailsManager.setRolePrefix("");  // -> ROLE_ as prefix still required

        return userDetailsManager;
    }
}
