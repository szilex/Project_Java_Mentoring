package com.euvic.mentoring.user;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class UserControllerTestConfiguration {

    @Bean
    DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("user");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
    }

    /*@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new CustomBasicAuthenticationEntryPoint();
    }*/


}
