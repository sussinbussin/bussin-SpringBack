package com.bussin.SpringBack.signUpTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.models.user.SignUpUniqueRequest;
import com.bussin.SpringBack.models.user.SignUpUniqueResponse;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SignUpIntegrationTests {
    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;

    /**
     * Check user with a valid and unique parameters when creating account success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void uniqueCheck_validUser_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        SignUpUniqueRequest signUpUniqueRequest = SignUpUniqueRequest.builder()
                                                                     .nric(userDTO.getNric())
                                                                     .email(userDTO.getEmail())
                                                                     .mobile(userDTO.getMobile())
                                                                     .build();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/unique");

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(signUpUniqueRequest));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(SignUpUniqueResponse.builder()
                                         .emailUnique(true)
                                         .nricUnique(true)
                                         .mobileUnique(true)
                                         .build(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), SignUpUniqueResponse.class));
    }

    /**
     * Check user with duplicated parameters when creating account returns all false
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void uniqueCheck_duplicateUser_allFalse() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userService.createNewUser(userDTO);

        SignUpUniqueRequest signUpUniqueRequest = SignUpUniqueRequest.builder()
                                                                     .nric(userDTO.getNric())
                                                                     .email(userDTO.getEmail())
                                                                     .mobile(userDTO.getMobile())
                                                                     .build();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/unique");

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(signUpUniqueRequest));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(SignUpUniqueResponse.builder()
                                         .emailUnique(false)
                                         .nricUnique(false)
                                         .mobileUnique(false)
                                         .build(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), SignUpUniqueResponse.class));
    }

    /**
     * Check user with any parameters that is duplicated when creating account will return false
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void uniqueCheck_nulls_mixed() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userService.createNewUser(userDTO);

        SignUpUniqueRequest signUpUniqueRequest = SignUpUniqueRequest.builder()
                                                                     .mobile(userDTO.getMobile())
                                                                     .build();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/unique");

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(signUpUniqueRequest));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(SignUpUniqueResponse.builder()
                                         .emailUnique(true)
                                         .nricUnique(true)
                                         .mobileUnique(false)
                                         .build(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), SignUpUniqueResponse.class));
    }
}
