package com.bussin.SpringBack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private TokenValidator tokenValidator;
    @Autowired
    private void setTokenValidator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.
                antMatcher("/**")
                    .csrf()
                    .disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilterBefore(tokenAuthFilter(),
                            UsernamePasswordAuthenticationFilter.class
                    )
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and().
                build();
    }

    @Bean
    public TokenAuthFilter tokenAuthFilter() {
        TokenAuthFilter authenticationFilter = new TokenAuthFilter();
        authenticationFilter.setAuthenticationManager(authentication -> {
            tokenValidator.userFromToken((String) authentication.getCredentials());
            authentication.setAuthenticated(true);
            return authentication;
        });
        return authenticationFilter;
    }
}