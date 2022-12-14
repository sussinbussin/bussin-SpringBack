package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserReadIntegrationTests {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CognitoLogin cognitoLogin;
    private String idToken;
    private User user;

    /**
     * Authenticate JWTToken and create a new TestObject user
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        user = userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Get no users when there are no users success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllUsers_noUsers_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
    }

    /**
     * Get all users when there are users success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllUsers_success() throws IOException {
        UserDTO userDTO = TestObjects.COGNITO_USER_DTO.clone();
        userDTO.setIsDriver(false);

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {
                        });

        UserDTO dest = UserDTO.builder().build();
        modelMapper.map(users.get(0), dest);
        assertEquals(dest.getNric(), userDTO.getNric());
    }

    /**
     * Get all details of a specific user if user exists success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getFullUserById_userExists_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users"
                + "/full/" + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        System.out.println(httpResponse.getEntity().getContent());

        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    /**
     * Get details of a specific user if user exists success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getUserById_userExists_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users/"
                + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    /**
     * Get user by ID when user doesn't exist throws 403 FORBIDDEN
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getUserById_userDoesntExist_403() throws IOException {
        HttpUriRequest request =
                new HttpGet(baseUrl + port
                        + "/api/v1/users/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(403, httpResponse.getCode());
    }

    /**
     * Get user by ID when bad UUID is provided throws 400 BAD_REQUEST
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getUserById_badUUID_400() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/users/beans");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Get user by email when user exist success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getUserByEmail_userExists_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users" +
                "/byEmail/"
                + user.getEmail());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    /**
     * Get user by email when user doesn't exist throws 403 FORBIDDEN
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getUserByEmail_userDoesntExist_403() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users" +
                "/byEmail/Robert@gmail.com");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(403, httpResponse.getCode());
    }
}
