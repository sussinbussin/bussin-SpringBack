package com.bussin.SpringBack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private TokenValidator tokenValidator;
    @Autowired
    private void setTokenValidator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    private FilterChainExceptionHandler filterChainExceptionHandler;
    @Autowired
    private void setFilterChainExceptionHandler(FilterChainExceptionHandler filterChainExceptionHandler) {
        this.filterChainExceptionHandler = filterChainExceptionHandler;
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
                    .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
                    .authorizeRequests()
                    .antMatchers("/users/**", "/driver/**", "/planned/**",
                            "/ride/**")
                    .authenticated()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .build();
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