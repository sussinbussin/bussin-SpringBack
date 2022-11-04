package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserUpdateIntegrationTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CognitoLogin cognitoLogin;

    private String idToken;

    private User user;

    private static final String AUTHORIZATION_HEADER = "Authorization";


    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        user = userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Update a user by ID when ID exist success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void updateUserById_success() throws IOException {
        UserDTO userDTOUpdated = TestObjects.COGNITO_USER_DTO.clone();
        userDTOUpdated.setIsDriver(false);
        userDTOUpdated.setName("Testing123");

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);
        StringEntity entity =
                new StringEntity(
                        new ObjectMapper().writeValueAsString(userDTOUpdated));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(userDTOUpdated.getName(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getName());
    }

    /**
     * Update a user when user is invalid throws 400 BAD_REQUEST
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void updateUser_invalidUser_400() throws IOException {
        UserDTO userDTOInvalidUpdate = UserDTO.builder()
                .nric("S9999999Z22")
                .name("Robert")
                .dob(new Date(90000000))
                .email("Robert@gmail.com")
                .mobile("900090000")
                .isDriver(false).build();

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(userDTOInvalidUpdate));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Update user when user does not exist throws 403 FORBIDDEN
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void updateUser_userDoesntExist_403() throws IOException {
        UserDTO userDTOUpdated = TestObjects.COGNITO_USER_DTO.clone();
        userDTOUpdated.setIsDriver(false);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(userDTOUpdated));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(403, httpResponse.getCode());
    }
}
