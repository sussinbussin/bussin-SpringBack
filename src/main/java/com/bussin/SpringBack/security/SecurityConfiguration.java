package com.bussin.SpringBack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationConfiguration configuration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().authenticated()
                )
                .addFilterAt(tokenAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        TokenAuthenticationFilter authenticationFilter = new TokenAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(configuration.getAuthenticationManager());
        return authenticationFilter;
    }
}