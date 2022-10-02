package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTests {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Test
    void contextLoads() {

    }

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    UserDTO userDTO = TestObjects.USER_DTO.clone();

    @Test
    public void getAllUsers_noUsers_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
    }

    @Test
    public void getAllUsers_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                        new TypeReference<>() {
                        });

        //Because ID will change
        userDTO.setId(user.getId());

        UserDTO dest = UserDTO.builder().build();
        modelMapper.map(users.get(0), dest);
        assertEquals(dest, userDTO);
    }

    @Test
    public void getFullUserById_userExists_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users"
                + "/full/" + user.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        System.out.println(httpResponse.getEntity().getContent());

        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    @Test
    public void getUserById_userExists_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users/"
                + user.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    @Test
    public void getUserById_userDoesntExist_404() throws IOException {
        HttpUriRequest request =
                new HttpGet(baseUrl + port
                        + "/api/v1/users/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 404);
    }

    @Test
    public void getUserById_badUUID_400() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/users/beans");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void getUserByEmail_userExists_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users" +
                "/byEmail/"
                + user.getEmail());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
        assertEquals(user.getId(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getId());
    }

    @Test
    public void getUserByEmail_userDoesntExist_404() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users" +
                "/byEmail/Robert@gmail.com");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 404);
    }

    // TODO: getUserByEmail_badEmail_400 test

    @Test
    public void createNewUser_validUser_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setId(null);
        userDTO.setIsDriver(false);

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users");
        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
        assertEquals(userDTO.getNric(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getNric());
    }

    @Test
    public void createNewUser_invalidUser_400() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);
        userDTO.setMobile("6969696969");

        HttpUriRequest request = new HttpPost(baseUrl + port
                + "/api/v1/users");
        StringEntity entity =
                new StringEntity(new ObjectMapper().writeValueAsString(userDTO));
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 400);
    }

    @Test
    public void updateUserById_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);

        UserDTO userDTOUpdated = this.userDTO;
        userDTO.setIsDriver(false);
        userDTO.setName("Testing123");

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + user.getId());
        StringEntity entity =
                new StringEntity(
                        new ObjectMapper().writeValueAsString(userDTOUpdated));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
        assertEquals(userDTOUpdated.getName(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getName());
    }

    @Test
    public void updateUser_invalidUser_400() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        UserDTO userDTOInvalidUpdate = UserDTO.builder()
                .nric("S9999999Z22")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("900090000")
                .isDriver(false).build();

        User user = userService.createNewUser(userDTO);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + user.getId());

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(userDTOInvalidUpdate));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void updateUser_userDoesntExist_404() throws IOException {
        UserDTO userDTOUpdated = this.userDTO;
        userDTOUpdated.setIsDriver(false);

        HttpUriRequest request = new HttpPut(baseUrl + port
                + "/api/v1/users/" + UUID.randomUUID());

        StringEntity entity = new StringEntity(new ObjectMapper()
                .writeValueAsString(userDTOUpdated));

        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 404);
    }

    @Test
    public void deleteUser_success() throws IOException {
        UserDTO userDTO = this.userDTO;
        userDTO.setIsDriver(false);

        User user = userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/users/" + user.getId());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 200);
        assertEquals(user.getName(),
                new ObjectMapper().readValue(
                        httpResponse.getEntity().getContent(), User.class).getName());
    }

    @Test
    public void deleteUser_userDoesntExist_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/users/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(httpResponse.getCode(), 404);
    }
}
