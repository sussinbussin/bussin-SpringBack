package com.bussin.SpringBack.plannedRouteTests;

import com.bussin.SpringBack.models.*;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.PlannedRouteService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
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
import java.time.LocalDateTime;
import java.util.Date;

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
    private PlannedRouteService plannedRouteService;

    @Test
    public void getAllRoutes_noRoutes_Success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

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
                .fuelType("Premium")
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
                "/planned/a6bb7dc3-5cbb-4408-a749-514e0b4a05d3");

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
                .fuelType("Premium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

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
                .fuelType("Premium")
                .build();

        driverService.addNewDriver(user.getId(), driverDTO);

        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3000)
                .build();

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
        PlannedRouteDTO plannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("111111")
                .plannedTo("222222")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

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
                .fuelType("Premium")
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

        PlannedRouteDTO updatedPlannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("222222")
                .plannedTo("333333")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(6)
                .build();

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
                .fuelType("Premium")
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

        PlannedRouteDTO updatedPlannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("222222")
                .plannedTo("333333")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3000)
                .build();

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
        PlannedRouteDTO updatedPlannedRouteDTO = PlannedRouteDTO.builder()
                .plannedFrom("222222")
                .plannedTo("333333")
                .dateTime(LocalDateTime.of(2022, 6, 6, 6, 6))
                .capacity(3)
                .build();

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
                .fuelType("Premium")
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
