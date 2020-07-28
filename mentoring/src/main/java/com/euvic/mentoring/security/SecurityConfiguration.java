package com.euvic.mentoring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final CustomBasicAuthenticationEntryPoint authenticationEntryPoint;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(DataSource dataSource, CustomBasicAuthenticationEntryPoint authenticationEntryPoint, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("SELECT mail, password, enabled FROM users WHERE mail=?")
                .authoritiesByUsernameQuery("SELECT mail, authority FROM users WHERE mail=?")
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestCache()
                .requestCache(new NullRequestCache())
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/meeting", "/meeting/{\\d+}", "/meeting/student/{\\d+}").hasAnyRole("MENTOR", "STUDENT")
                .antMatchers(HttpMethod.POST,"/meeting").hasRole("MENTOR")
                .antMatchers(HttpMethod.PUT,"/meeting").hasRole("STUDENT")
                .antMatchers(HttpMethod.DELETE,"/meeting/{\\d+}").hasRole("MENTOR")
                .antMatchers("/meeting", "/meeting/", "/meeting/{\\d+}").fullyAuthenticated()
                .antMatchers("/user/mentor", "/user/mentor/{\\d+}").hasRole("MENTOR")
                .antMatchers(HttpMethod.GET,"/user/student", "user/student/{\\d+}").access("isAuthenticated() and hasAnyRole('MENTOR','STUDENT')")
                .antMatchers(HttpMethod.POST,"/user/student").access("!isAuthenticated()")
                .antMatchers(HttpMethod.PUT,"/user/student").access("isAuthenticated() and hasRole('STUDENT')")
                .antMatchers(HttpMethod.DELETE,"user/student/{\\d+}").access("isAuthenticated() and hasRole('STUDENT')")
                .and()
            .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf()
                .disable();
    }
}