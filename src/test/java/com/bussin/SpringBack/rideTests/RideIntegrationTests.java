package com.bussin.SpringBack.rideTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.PlannedRoute;
import com.bussin.SpringBack.models.PlannedRouteDTO;
import com.bussin.SpringBack.models.Ride;
import com.bussin.SpringBack.models.RideCreationDTO;
import com.bussin.SpringBack.models.RideDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
import com.bussin.SpringBack.services.RideService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RideIntegrationTests {
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

    @Test
    public void getAllRides_noRides_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

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

        Ride ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<Ride> rides =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {});

        rideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rides.get(0), dest);
        assertEquals(dest, rideDTO);
    }

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

        Ride ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + ride.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Ride rideResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        Ride.class);

        rideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rideResult, dest);
        assertEquals(dest, rideDTO);
    }

    @Test
    public void getRideByID_noRide_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void createNewRide_success() throws IOException {
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

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(plannedRoute.getId())
                .userUUID(user.getId())
                .build();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/ride/");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(rideCreationDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Ride rideResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        Ride.class);

        rideDTO.setId(rideResult.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rideResult, dest);
        assertEquals(dest, rideDTO);
    }

    @Test
    public void createNewRide_invalidParams_400() throws IOException {
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
        rideDTO.setPassengers(1000);

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(plannedRoute.getId())
                .userUUID(user.getId())
                .build();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/ride/");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(rideCreationDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void createNewRide_missingUser_404() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(plannedRoute.getId())
                .userUUID(UUID.randomUUID())
                .build();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/ride/");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(rideCreationDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void createNewRide_noRoute_404() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();

        plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO.clone();

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(UUID.randomUUID())
                .userUUID(user.getId())
                .build();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/ride/");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(rideCreationDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void updateRideById_success() throws IOException {
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

        RideDTO updatedRideDTO = TestObjects.RIDE_DTO.clone();
        updatedRideDTO.setPassengers(2);

        Ride ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/ride/" + ride.getId());

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedRideDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Ride rideResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        Ride.class);

        updatedRideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rideResult, dest);
        assertEquals(dest, updatedRideDTO);
    }

    @Test
    public void updateRideById_invalidParams_400() throws IOException {
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

        RideDTO updatedRideDTO = RideDTO.builder()
                .passengers(1000)
                .timestamp(rideDTO.getTimestamp())
                .build();

        Ride ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/ride/" + ride.getId());

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedRideDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void updateRideById_noRide_404() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setCapacity(3);

        plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                driverDTO.getCarPlate());

        RideDTO updatedRideDTO = TestObjects.RIDE_DTO.clone();
        updatedRideDTO.setPassengers(3);

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/ride/" + "a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedRideDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void deleteRideByID_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = TestObjects.PLANNED_ROUTE_DTO;

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = TestObjects.RIDE_DTO;

        Ride ride = rideService.createNewRide(rideDTO, user.getId(),
                plannedRoute.getId());

        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/ride/" + ride.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Ride rideResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        Ride.class);

        rideDTO.setId(ride.getId());

        RideDTO dest = RideDTO.builder().build();
        modelMapper.map(rideResult, dest);
        assertEquals(dest, rideDTO);
    }

    @Test
    public void deleteRideByID_noRide_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/ride/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
