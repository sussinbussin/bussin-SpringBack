package com.bussin.SpringBack.rideTests;

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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

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
                "/ride/" + "a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void createNewRide_success() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(222222)
                .timestamp(new Timestamp(now))
                .build();

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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(2)
                .timestamp(new Timestamp(now))
                .build();

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(plannedRoute.getId())
                .userUUID(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(2)
                .timestamp(new Timestamp(now))
                .build();

        RideCreationDTO rideCreationDTO = RideCreationDTO.builder()
                .rideDTO(rideDTO)
                .plannedRouteUUID(UUID.fromString("a6bb7dc3-5cbb-4408-a749-514e0b4a05d3"))
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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

        RideDTO updatedRideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

        RideDTO updatedRideDTO = RideDTO.builder()
                .passengers(1000)
                .timestamp(new Timestamp(now))
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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO updatedRideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

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
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        long now = System.currentTimeMillis();
        RideDTO rideDTO = RideDTO.builder()
                .passengers(1)
                .timestamp(new Timestamp(now))
                .build();

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
                "/ride/" + "a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
