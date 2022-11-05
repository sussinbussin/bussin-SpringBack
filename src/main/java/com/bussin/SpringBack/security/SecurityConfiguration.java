package com.bussin.SpringBack.security;

import com.amazonaws.services.xray.model.Http;
import com.bussin.SpringBack.models.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${debugMode}")
    private boolean debugMode = false;

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

    /**
     * Configures the filter chain based on whether debugMode is enabled
     * @param http HttpSecurity instance to configure
     * @return The configured HttpSecurity instance
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return (debugMode ? configureFilterChain(http) :
                    configureFilterChain(debugRestrictions(http))).build();
    }

    private HttpSecurity configureFilterChain(HttpSecurity http) throws Exception {
        return http.
                mvcMatcher("/**")
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

                .mvcMatchers(HttpMethod.GET, "/users/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.PUT, "/users/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/users/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.GET, "/users/full/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.GET, "/users/byEmail/*")
                    .authenticated()

                .mvcMatchers(HttpMethod.GET, "/driver/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.POST, "/driver/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.PUT, "/driver/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.DELETE, "/driver/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.GET, "/driver/*/plannedRoutes")
                    .hasAuthority("Driver")

                .mvcMatchers(HttpMethod.GET, "/planned/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.GET, "/planned/*/passengers")
                    .authenticated()
                .mvcMatchers(HttpMethod.GET, "/planned/after/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.PUT, "/planned/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.POST, "/planned/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.DELETE, "/planned/*")
                    .hasAuthority("Driver")

                .mvcMatchers(HttpMethod.GET, "/ride/*")
                    .authenticated()
                .mvcMatchers(HttpMethod.POST,"/ride")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.PUT, "/ride/*")
                    .hasAuthority("Driver")
                .mvcMatchers(HttpMethod.DELETE, "/ride/*")
                    .hasAuthority("Driver")

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and();
    }

    private HttpSecurity debugRestrictions(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET,
                        "/users",
                        "/driver",
                        "/planned",
                        "/ride")
                    .denyAll()
                .mvcMatchers(HttpMethod.POST, "/users")
                    .denyAll()
                .and();
    }

    @Bean
    public TokenAuthFilter tokenAuthFilter() {
        TokenAuthFilter authenticationFilter = new TokenAuthFilter();
        authenticationFilter.setRequiresAuthenticationRequestMatcher(getMatcher());

        authenticationFilter.setAuthenticationManager(incoming -> {
            List<GrantedAuthority> authorities =
                    new ArrayList<>(List.of(new SimpleGrantedAuthority("User")));
            User user =
                    tokenValidator.userFromToken((String) incoming.getCredentials());
            if(user.getIsDriver()){
                authorities.add(new SimpleGrantedAuthority("Driver"));
            }

            Authentication auth = new PreAuthenticatedAuthenticationToken(user,
                    incoming.getCredentials(), authorities);
            auth.setAuthenticated(true);

            return auth;
        });
        return authenticationFilter;
    }

    private RequestMatcher getMatcher() {
        ArrayList<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AntPathRequestMatcher("/users"));
        matchers.add(new AntPathRequestMatcher("/users/*"));
        matchers.add(new AntPathRequestMatcher( "/users/full/*"));
        matchers.add(new AntPathRequestMatcher("/users/byEmail/*"));
        matchers.add(new AntPathRequestMatcher("/driver/**"));
        matchers.add(new AntPathRequestMatcher("/planned/**"));
        matchers.add(new AntPathRequestMatcher("/ride/**"));
        return new OrRequestMatcher(matchers);
    }
}
