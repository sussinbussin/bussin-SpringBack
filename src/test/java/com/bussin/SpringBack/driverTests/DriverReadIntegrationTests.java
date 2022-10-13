package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.DriverService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DriverReadIntegrationTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Test
    void contextLoads() {

    }

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @Autowired
    private CognitoLogin cognitoLogin;

    private String idToken;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     * @throws IOException
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken();
        userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Get no drivers when there are no drivers success
     * @throws IOException
     */
    @Test
    public void getAllDrivers_noDrivers_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

    /**
     * Get all drivers when there are drivers success
     * @throws IOException
     */
    @Test
    public void getAllDrivers_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        driverService.addNewDriver(user.getId(), driverDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver/");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Driver> drivers =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {
                        });

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(drivers.get(0), dest);
        assertEquals(dest, driverDTO);
    }

    /**
     * Get driver by car plate when car plate exist success
     * @throws IOException
     */
    @Test
    public void getDriverByCarPlate_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        driverService.addNewDriver(user.getId(), driverDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

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
     * Get driver by car plate when no car plate exist throws 404 NOT_FOUND
     * @throws IOException
     */
    @Test
    public void getDriverByCarPlate_noDriver_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/Beans");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
