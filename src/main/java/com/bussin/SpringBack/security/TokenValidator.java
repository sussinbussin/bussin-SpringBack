package com.bussin.SpringBack.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TokenValidator {
    private UserService userService;

    @Value("${region}")
    private String aws_cognito_region;
    @Value("${userPoolId}")
    private String aws_user_pools_id;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the related user from the token.
     * @param token The token to get the user from
     * @return The user associated with the token
     */
    public User userFromToken(String token) {
        try {
            return userService.getFullUserByEmail(validateToken(token));
        } catch (UsernameNotFoundException | JsonProcessingException e) {
            throw new JWTVerificationException("Bad credentials");
        }
    }

    /**
     * Validates a token.
     * @param token The token to validate
     * @return The email address from the validated token
     * @throws JsonProcessingException
     */
    public String validateToken(String token) throws JsonProcessingException {
        RSAKeyProvider keyProvider =
                new AwsCognitoRSAKeyProvider(aws_cognito_region, aws_user_pools_id);
        Algorithm algorithm = Algorithm.RSA256(keyProvider);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .acceptNotBefore(10)
                .build();

        DecodedJWT decodedJWT = jwtVerifier.verify(getTrimmedToken(token));

        ObjectNode node = new ObjectMapper()
                .readValue(new String(Base64.getDecoder().decode(decodedJWT.getPayload())), ObjectNode.class);
        return node.get("email").asText().replaceAll("^\"|\"$", "");
    }

    /**
     * Gets a trimmed, formatted token.
     * @param token The token to trim
     * @return The trimmed token
     */
    public String getTrimmedToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        throw new JWTVerificationException("Cannot log in");
    }
}
