package com.bussin.SpringBack.rideTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.ride.Ride;
import com.bussin.SpringBack.models.ride.RideDTO;
import com.bussin.SpringBack.models.ride.RideReturnDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
import com.bussin.SpringBack.services.RideService;
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
public class RideReadIntegrationTests {
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
    private PlannedRouteService plannedRouteService;

    @Autowired
    private RideService rideService;

    @Autowired
    private CognitoLogin cognitoLogin;

    private String idToken;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Get all rides when no rides are found success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllRides_noRides_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

    /**
     * Get all rides when rides exist are found success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllRides_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        RideReturnDTO ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<Ride> rides =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {
                        });

        rideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rides.get(0), dest);
        assertEquals(dest, rideDTO);
    }

    /**
     * Get a ride by ID success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getRideByID_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        RideReturnDTO ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + ride.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        RideReturnDTO rideResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        RideReturnDTO.class);

        rideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rideResult, dest);
        assertEquals(dest, rideDTO);
    }

    /**
     * Get a ride by ID when no ride is found throws 404 NOT_FOUND
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getRideByID_noRide_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
