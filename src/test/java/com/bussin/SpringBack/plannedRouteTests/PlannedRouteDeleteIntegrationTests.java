package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoutePublicDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.models.user.UserDTO;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
import com.bussin.SpringBack.services.RideService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlannedRouteDeleteIntegrationTests {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;
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

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(true);
        userService.createNewUser(TestObjects.COGNITO_DRIVER_DTO.clone());
    }

    /**
     * Delete a planned route with planned route found success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void deletePlannedRoute_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoutePublicDTO plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoutePublicDTO.class);

        plannedRouteDTO.setId(plannedRouteResult.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(dest, plannedRouteDTO);
    }

    /**
     * Delete a planned route with no route found throws 404 NOT_FOUND
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void deletePlannedRoute_noRoute_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
