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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DriverUpdateIntegrationTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

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
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken();
        userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Update a driver with new valid credentials success
     */
    @Test
    public void updateDriver_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        DriverDTO updatedDriverDTO = TestObjects.DRIVER_DTO.clone();
        updatedDriverDTO.setModelAndColour("Clown Car");
        updatedDriverDTO.setCapacity(12);
        updatedDriverDTO.setFuelType("TypePremium");

        User user = userService.createNewUser(userDTO);
        Driver driver = driverService.addNewDriver(user.getId(), driverDTO);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(updatedDriverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Driver driverResult = new ObjectMapper()
                .readValue(httpResponse.getEntity().getContent(), Driver.class);

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(driverResult, dest);

        assertEquals(dest, updatedDriverDTO);
        assertEquals(driver.getUser().getId(), user.getId());
    }

    /**
     * Update a driver when car plate is not found throws 404 NOT_FOUND
     */
    @Test
    public void updateDriver_noDriver_404() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO updatedDriverDTO = TestObjects.DRIVER_DTO.clone();

        userService.createNewUser(userDTO);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/driver/" + updatedDriverDTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(updatedDriverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    /**
     * Update a driver with invalid credentials throws 400 BAD_REQUEST
     */
    @Test
    public void updateDriver_badParams_400() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        DriverDTO updatedDriverDTO = TestObjects.DRIVER_DTO.clone();
        updatedDriverDTO.setModelAndColour("Actual clown car");
        updatedDriverDTO.setCapacity(12000);

        User user = userService.createNewUser(userDTO);
        driverService.addNewDriver(user.getId(), driverDTO);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(updatedDriverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }
}
