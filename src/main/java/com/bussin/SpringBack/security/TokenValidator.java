package com.bussin.SpringBack.security;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenValidator {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User userFromToken(String token) {
        try {
            return userService.getFullUserByEmail(validateToken(token));
        } catch (UsernameNotFoundException | JsonProcessingException e) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public static String validateToken(String token) throws JsonProcessingException {
        if (token != null && token.startsWith("Bearer ")) {
            String trimmedToken = token.substring(7).trim();

            String aws_cognito_region = "ap-southeast-1";
            String aws_user_pools_id = "ap-southeast-1_cAJ9EgqL1";
            RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, aws_user_pools_id);
            Algorithm algorithm = Algorithm.RSA256(keyProvider);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .build();

            DecodedJWT decodedJWT = jwtVerifier.verify(trimmedToken);
            if (decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
                throw new BadCredentialsException("Expired");
            }

            ObjectNode node = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(decodedJWT.getPayload())), ObjectNode.class);
            return node.get("email").asText().replaceAll("^\"|\"$", "");
        }
        throw new BadCredentialsException("Cannot log in");
    }
}
