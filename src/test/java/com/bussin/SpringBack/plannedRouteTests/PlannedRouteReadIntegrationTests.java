package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.ride.RideDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.models.user.UserPublicDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlannedRouteReadIntegrationTests {
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
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(false);
        userService.createNewUser(TestObjects.COGNITO_USER_DTO);
    }

    /**
     * Get all routes when no routes exist success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllRoutes_noRoutes_Success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
    }

    /**
     * Get all planned routes success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllPlannedRoutes_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute = plannedRouteService
                .createNewPlannedRoute(plannedRouteDTO, driverDTO.getCarPlate());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<PlannedRoute> plannedRoutes =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {});

        plannedRouteDTO.setId(plannedRoute.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRoutes.get(0), dest);
        assertEquals(dest, plannedRouteDTO);
    }

    /**
     * Get a planned route by id success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getPlannedRouteById_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoutePublicDTO plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoutePublicDTO.class);

        plannedRouteDTO.setId(plannedRoute.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(plannedRouteDTO, dest);
    }

    /**
     * Get a planned route by id when planned route doesn't exist throws 404 NOT_FOUND
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getPlannedRouteById_doesntExist_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    /**
     * Get all passengers on a planned route success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getPassengersOnRoute_Success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        UserDTO passengerDTO = TestObjects.USER_DTO.clone();
        passengerDTO.setNric("S6969691Z");
        passengerDTO.setEmail("another@test.com");
        passengerDTO.setMobile("89898989");

        User passenger = userService.createNewUser(passengerDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        rideService.createNewRide(rideDTO, passenger.getId(),
                plannedRoute.getId());

        List<UserPublicDTO> expected = new ArrayList<>();
        expected.add(modelMapper.map(passenger, UserPublicDTO.class));

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId() + "/passengers");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<UserPublicDTO> result = objectMapper.readValue(
                httpResponse.getEntity().getContent(),
                new TypeReference<>() {
                });

        assertThat(result).hasSameElementsAs(expected);
    }

    /**
     * Get passengers on a planned route when planned route doesn't exist throws 404 NOT_FOUND
     * @throws IOException
     */
    @Test
    public void getPassengersOnRoute_noRoute_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID() + "/passengers");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
