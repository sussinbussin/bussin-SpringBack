 package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.models.Driver;
import com.bussin.SpringBack.models.DriverDTO;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DriverIntegrationTests {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Test
    void contextLoads() {

    }

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @Test
    public void getAllDrivers_noDrivers_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

    @Test
    public void getAllDrivers_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        driverService.addNewDriver(user.getId(), driverDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/driver/");

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

    @Test
    public void getDriverByCarPlate_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        driverService.addNewDriver(user.getId(), driverDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Driver driver = new ObjectMapper()
                .readValue(httpResponse.getEntity().getContent(), Driver.class);

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(driver, dest);

        assertEquals(dest, driverDTO);
        assertEquals(driver.getUser().getId(), user.getId());
    }

    @Test
    public void getDriverByCarPlate_noDriver_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/Beans");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

    @Test
    public void addNewDriver_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + user.getId());

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Driver driver = new ObjectMapper()
                .readValue(httpResponse.getEntity().getContent(), Driver.class);

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(driver, dest);

        assertEquals(dest, driverDTO);
        assertEquals(driver.getUser().getId(), user.getId());
    }

    @Test
    public void addNewDriver_noUser_404() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + UUID.randomUUID());

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 404);
    }

    @Test
    public void addNewDriver_badParams_400() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();
        driverDTO.setCapacity(1000);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/driver/" + user.getId());

        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(driverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 400);
    }

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

    @Test
    public void updateDriver_noDriver_404() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO updatedDriverDTO = TestObjects.DRIVER_DTO.clone();

        userService.createNewUser(userDTO);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/driver/" + updatedDriverDTO.getCarPlate());

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(updatedDriverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }

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

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(updatedDriverDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void deleteDriver_success() throws IOException {
        UserDTO userDTO = TestObjects.USER_DTO.clone();
        userDTO.setIsDriver(true);

        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        User user = userService.createNewUser(userDTO);
        Driver driver = driverService.addNewDriver(user.getId(), driverDTO);

        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        Driver driverResult = new ObjectMapper()
                .readValue(httpResponse.getEntity().getContent(), Driver.class);

        DriverDTO dest = DriverDTO.builder().build();
        modelMapper.map(driverResult, dest);

        assertEquals(dest, driverDTO);
        assertEquals(driver.getUser().getId(), user.getId());
        assert (!userService.getUserById(user.getId()).getIsDriver());
    }

    @Test
    public void deleteDriver_noDriver_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/driver/" + "SSS13370Z");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
