package com.bussin.SpringBack.integrationTestAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;

@Service
public class CognitoLogin {
    static final String COGNITO_URL = "https://cognito-idp.ap-southeast-1" +
            ".amazonaws.com/";
    private final ObjectMapper objectMapper;
    @Value("${clientId}")
    private String cognitoClientId;
    @Value("${cognito.username}")
    private String cognitoUsername;
    @Value("${cognito.driverName}")
    private String cognitoDriverName;
    @Value("${cognito.password}")
    private String cognitoPassword;

    @Autowired
    public CognitoLogin(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Gets the authentication ID token of the authenticated user
     *
     * @param isDriver A boolean to check if user is a driver
     * @return A string of authenticated ID token
     * @throws IOException If an input or output exception occurred
     */
    public String getAuthToken(final boolean isDriver) throws IOException {
        AuthParameters authParameters = isDriver ?
                new AuthParameters(cognitoDriverName, cognitoPassword) :
                new AuthParameters(cognitoUsername, cognitoPassword);

        LoginRequestModel loginRequestModel = new LoginRequestModel(
                "USER_PASSWORD_AUTH", cognitoClientId, authParameters);

        HttpUriRequest request = new HttpPost(COGNITO_URL);
        StringEntity entity =
                new StringEntity(objectMapper.writeValueAsString(loginRequestModel));
        request.setEntity(entity);
        request.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");
        request.setHeader("Content-Type", "application/x-amz-json-1.1");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        return objectMapper.readValue(
                httpResponse.getEntity().getContent(),
                AuthResponse.class).getAuthenticationResult().getIdToken();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginRequestModel implements Serializable {
        @JsonProperty("AuthFlow")
        private String authFlow = "USER_PASSWORD_AUTH";

        @JsonProperty("ClientId")
        private String clientId;

        @JsonProperty("AuthParameters")
        private AuthParameters authParameters;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class AuthParameters implements Serializable {
        @JsonProperty("USERNAME")
        private String username;

        @JsonProperty("PASSWORD")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AuthenticationResult implements Serializable {
        @JsonProperty("IdToken")
        private String idToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AuthResponse implements Serializable {
        @JsonProperty("AuthenticationResult")
        private AuthenticationResult authenticationResult;
    }
}
