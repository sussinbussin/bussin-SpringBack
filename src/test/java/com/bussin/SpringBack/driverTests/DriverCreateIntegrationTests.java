package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.services.DriverService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DriverCreateIntegrationTests {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DriverService driverService;
    @Autowired
    private UserService userService;
    @Autowired
    private CognitoLogin cognitoLogin;
    private String idToken;
    private User user;

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        user = userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Create a new driver with valid credentials success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void addNewDriver_success() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Driver driver = new ObjectMapper()
                .readValue(httpResponse.getEntity().getContent(), Driver.class);

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(driver, dest);

        assertEquals(dest, driverDTO);
        assertEquals(driver.getUser().getId(), user.getId());
    }

    /**
     * Create a new driver with no user found throws 404 NOT_FOUND
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void addNewDriver_noUser_403() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(403, httpResponse.getCode());
    }

    /**
     * Create a new driver with invalid parameters throws 400 BAD_REQUEST
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void addNewDriver_invalidParams_400() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCapacity(1000);

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + user.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }
}
