package com.bussin.SpringBack.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

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
                    .antMatchers(
                            "/users/*",
                            "/users/full/*",
                            "/users/byEmail/*",
                            "/planned/**",
                            "/ride/**")
                    .authenticated()
                    .antMatchers(HttpMethod.POST,"/driver/*").authenticated()
                        .antMatchers("/driver/**")
                        .hasAuthority("Driver")
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .build();
    }

    @Bean
    public TokenAuthFilter tokenAuthFilter() {
        TokenAuthFilter authenticationFilter = new TokenAuthFilter();
        ArrayList<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/users/*"));
        matchers.add(new AntPathRequestMatcher( "/users/full/*"));
        matchers.add(new AntPathRequestMatcher("/users/byEmail/*"));
        matchers.add(new AntPathRequestMatcher("/driver/**"));
        matchers.add(new AntPathRequestMatcher("/planned/**"));
        matchers.add(new AntPathRequestMatcher("/ride/**"));

        authenticationFilter.setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(matchers));
        authenticationFilter.setAuthenticationManager(incoming -> {
            List<GrantedAuthority> authorities = new ArrayList<>(List.of(new SimpleGrantedAuthority("User")));
            if(tokenValidator.userFromToken((String) incoming.getCredentials()).getIsDriver()){
                authorities.add(new SimpleGrantedAuthority("Driver"));
            }

            Authentication auth = new PreAuthenticatedAuthenticationToken("",
                    incoming.getCredentials(), authorities);
            auth.setAuthenticated(true);

            return auth;
        });
        return authenticationFilter;
    }
}