package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserCreationDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserCreateIntegrationTests {
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

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Create a new user with valid credentials success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void createNewUser_validUser_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setId(null);
        userDTO.setIsDriver(false);

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
        assertEquals(userDTO.getNric(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getNric());
    }

    /**
     * Create a new user with invalid credentials throws 400 BAD_REQUEST
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void createNewUser_invalidUser_400() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                                 .nric("S9999999Z")
                                 .name("Robert")
                                 .dob(new Date(90000000))
                                 .email("Robert@gmail.com")
                                 .mobile("6969696969")
                                 .isDriver(false).build();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Create a new user with invalid password throws 400 BAD REQUEST
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void createNewUser_invalidPassword_400() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                                 .nric("S1231233Z")
                                 .name("Robert")
                                 .dob(new Date(90000000))
                                 .email("uchawuhfa@gmail.com")
                                 .mobile("81729365")
                                 .isDriver(false)
                                 .build();

        UserCreationDTO userCreationDTO =
                new UserCreationDTO("simple", "sdsdhfics", userDTO);

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users/wCognito/create");

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userCreationDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Create a new user with duplicated username throws 400 BAD REQUEST
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void createNewUser_duplicatedUsername_400() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                                 .nric("S1231233Z")
                                 .name("Robert")
                                 .dob(new Date(90000000))
                                 .email("uchawuhfa@gmail.com")
                                 .mobile("81729365")
                                 .isDriver(false)
                                 .build();

        UserCreationDTO userCreationDTO =
                new UserCreationDTO("P@ssw0rd", "SpringBackTest", userDTO);

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users/wCognito/create");

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userCreationDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }
}
