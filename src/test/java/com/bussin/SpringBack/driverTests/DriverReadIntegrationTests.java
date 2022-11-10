package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRoute;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteDTO;
import com.bussin.SpringBack.models.plannedRoute.PlannedRouteResultDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
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
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DriverReadIntegrationTests {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String baseUrl = "http://localhost:";
    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DriverService driverService;
    @Autowired
    private PlannedRouteService plannedRouteService;
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
        idToken = "Bearer " + cognitoLogin.getAuthToken(true);
        user = userService.createNewUser(TestObjects.COGNITO_DRIVER_DTO);
    }

    /**
     * Get no drivers when there are no drivers success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllDrivers_noDrivers_200() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
    }

    /**
     * Get all drivers when there are drivers success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getAllDrivers_success() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

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
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getDriverByCarPlate_success() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

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
     * Get another driver by car plate when car plate exist, 403
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getDriverByCarPlate_anotherDriver_403() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        DriverDTO driverDTO1 = TestObjects.DRIVER_DTO.clone();
        driverDTO1.setCarPlate("SXX5555X");

        driverService.addNewDriver(user.getId(), driverDTO);

        Driver driver1 =
                driverService.addNewDriver(userService.createNewUser(
                        TestObjects.USER_DTO.clone()).getId(), driverDTO1);

        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/" + driver1.getCarPlate());

        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(403, httpResponse.getCode());
    }

    /**
     * Get driver by car plate when no car plate exist throws 404 NOT_FOUND
     *
     * @throws IOException If an input or output exception occurred
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

    /**
     * Get all planned routes from a driver success
     *
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getPlannedRoutesFromDriver_success() throws IOException {
        ModelMapper modelMapper
                = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<PlannedRoute, PlannedRouteResultDTO>() {
            @Override
            protected void configure() {
                map().setCarPlate(source.getDriver().getCarPlate());
            }
        });

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        PlannedRouteDTO plannedRouteDTO1 =
                TestObjects.PLANNED_ROUTE_DTO.clone();
        PlannedRouteDTO plannedRouteDTO2 =
                TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO2.setDateTime(LocalDateTime.of(2021, 6, 6, 6, 6));

        driverService.addNewDriver(user.getId(), driverDTO);
        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO1,
                        driverDTO.getCarPlate());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver" +
                "/" + driverDTO.getCarPlate() + "/plannedRoutes");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<PlannedRouteResultDTO> publicDTOS =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {
                        });

        assertEquals(
                modelMapper.map(plannedRoute,
                        PlannedRouteResultDTO.class).getId(),
                publicDTOS.get(0).getId());
    }
}
