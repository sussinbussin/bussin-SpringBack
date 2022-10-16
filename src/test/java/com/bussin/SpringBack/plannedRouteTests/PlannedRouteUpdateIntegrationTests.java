package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.*;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
import com.bussin.SpringBack.services.RideService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlannedRouteUpdateIntegrationTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RideService rideService;

    @Autowired
    private PlannedRouteService plannedRouteService;

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
     * Update a planned route with valid parameters success
     * @throws IOException
     */
    @Test
    public void updatePlannedRoute_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        PlannedRouteDTO updatedPlannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        updatedPlannedRouteDTO.setPlannedFrom("222222");
        updatedPlannedRouteDTO.setPlannedTo("333333");
        updatedPlannedRouteDTO.setCapacity(6);

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedPlannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoute plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoute.class);

        updatedPlannedRouteDTO.setId(plannedRoute.getId());
        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(dest, updatedPlannedRouteDTO);
    }

    /**
     * Update a planned route with invalid parameters throws 400 BAD_REQUEST
     * @throws IOException
     */
    @Test
    public void updatePlannedRoute_invalidParams_400() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        PlannedRouteDTO updatedPlannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        updatedPlannedRouteDTO.setCapacity(3000);

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedPlannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Update a planned route with no route found throws 404 NOT_FOUND
     * @throws IOException
     */
    @Test
    public void updatePlannedRoute_noRoute_404() throws IOException {
        PlannedRouteDTO updatedPlannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedPlannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
