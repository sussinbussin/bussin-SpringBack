package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.models.*;
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
import org.apache.hc.core5.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlannedRouteIntegrationTests {
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

    static final PlannedRouteDTO PLANNED_ROUTE_DTO = PlannedRouteDTO.builder()
            .plannedFrom("111111")
            .plannedTo("222222")
            .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
            .capacity(3)
            .build();

    @Test
    public void getAllRoutes_noRoutes_Success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

    //TODO Get All Routes, success

    @Test
    public void getPlannedRouteById_success() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoute plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoute.class);

        plannedRouteDTO.setId(plannedRoute.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(dest, plannedRouteDTO);
    }

    @Test
    public void getPlannedRouteById_doesntExist_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void getPassengersOnRoute_Success() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(true).build();

        User user = userService.createNewUser(userDTO);

        UserDTO passengerDTO = UserDTO.builder()
                .nric("S9999991Z")
                .name("Robert1")
                .dob(new Date(90000000))
                .address("123124")
                .email("Robert1@gmail.com")
                .mobile("90009001")
                .isDriver(true)
                .build();

        User passenger = userService.createNewUser(passengerDTO);

        DriverDTO driverDTO = DriverDTO.builder()
                .carPlate("SAA12345B")
                .modelAndColour("Flamingo MrBean Car")
                .capacity(2)
                .fuelType("TypePremium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        RideDTO rideDTO = RideDTO.builder()
            .passengers(1)
            .timestamp(new Timestamp(System.currentTimeMillis()))
            .build();

        rideService.createNewRide(rideDTO, passenger.getId(),
                plannedRoute.getId());

        List<UserPublicDTO> expected = new ArrayList<>();
        expected.add(modelMapper.map(passenger, UserPublicDTO.class));

        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId() + "/passengers");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        List<UserPublicDTO> result = objectMapper.readValue(
                httpResponse.getEntity().getContent(),
                new TypeReference<>() {
                });

        assertThat(result).hasSameElementsAs(expected);
    }

    @Test
    public void getPassengersOnRoute_noRoute_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID() + "/passengers");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void createPlannedRoute_success() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/planned/" + driverDTO.getCarPlate());
        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(plannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoute plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoute.class);

        plannedRouteDTO.setId(plannedRouteResult.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(dest, plannedRouteDTO);
    }

    @Test
    public void createPlannedRoute_invalidArgs_400() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();
        plannedRouteDTO.setCapacity(3000);

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/planned/" + driverDTO.getCarPlate());
        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(plannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void createPlannedRoute_noDriver_404() throws IOException {
        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1" +
                "/planned/" + "SAA1345A");
        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(plannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void updatePlannedRoute_success() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        PlannedRouteDTO updatedPlannedRouteDTO = PLANNED_ROUTE_DTO.clone();
        updatedPlannedRouteDTO.setPlannedFrom("222222");
        updatedPlannedRouteDTO.setPlannedTo("333333");
        updatedPlannedRouteDTO.setCapacity(6);

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());

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

    @Test
    public void updatePlannedRoute_invalidParams_400() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        PlannedRouteDTO updatedPlannedRouteDTO = PLANNED_ROUTE_DTO.clone();
        updatedPlannedRouteDTO.setCapacity(3000);

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedPlannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void updatePlannedRoute_noRoute_404() throws IOException {
        PlannedRouteDTO updatedPlannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        HttpUriRequest request = new HttpPut(baseUrl + port + "/api/v1" +
                "/planned/" + "a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        StringEntity entity = new StringEntity(objectMapper
                .writeValueAsString(updatedPlannedRouteDTO));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void deletePlannedRoute_success() throws IOException {
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

        PlannedRouteDTO plannedRouteDTO = PLANNED_ROUTE_DTO.clone();

        PlannedRoute plannedRoute =
                plannedRouteService.createNewPlannedRoute(plannedRouteDTO,
                        driverDTO.getCarPlate());

        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/planned/" + plannedRoute.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        PlannedRoute plannedRouteResult =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        PlannedRoute.class);

        plannedRouteDTO.setId(plannedRouteResult.getId());

        PlannedRouteDTO dest = PlannedRouteDTO.builder().build();
        modelMapper.map(plannedRouteResult, dest);
        assertEquals(dest, plannedRouteDTO);
    }

    @Test
    public void deletePlannedRoute_noRoute_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port + "/api/v1" +
                "/planned/" + "a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
