package com.bussin.SpringBack.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(
            "/login", "POST");

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public TokenAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String token = request.getHeader("Authentication");
        User user = userFromToken(token);

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(user, null,
                Collections.singletonList(new SimpleGrantedAuthority("user")));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }

    private User userFromToken(String token) {
        try {
            return userService.getFullUserByEmail(validateToken(token));
        } catch (UsernameNotFoundException | JsonProcessingException e) {
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    public static String validateToken(String token) throws JsonProcessingException {
        if (token != null && !token.isEmpty()) {
            String aws_cognito_region = "ap-southeast-1";
            String aws_user_pools_id = "ap-southeast-1_cAJ9EgqL1";
            RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, aws_user_pools_id);
            Algorithm algorithm = Algorithm.RSA256(keyProvider);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            if(decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis()))){
                throw new BadCredentialsException("Expired");
            }

            ObjectNode node = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(decodedJWT.getPayload())), ObjectNode.class);
            return node.get("cognito:username").asText().replaceAll("^\"|\"$", "");
        }
        throw new BadCredentialsException("Cannot log in");
    }
}