package com.bussin.SpringBack.userTests;

import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
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
    private UserService userService;

    @Test
    public void getAllUsers_noUsers_success() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute( request );

        assertEquals(httpResponse.getCode(), 200);
    }

    @Test
    public void getAllUsers_success() throws IOException {
        UserDTO userDTO = UserDTO.builder()
                .nric("S9999999Z")
                .name("Robert")
                .dob(new Date(90000000))
                .address("123123")
                .email("Robert@gmail.com")
                .mobile("90009000")
                .isDriver(false).build();

        userService.createNewUser(userDTO);
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute( request );

        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users =
                objectMapper.readValue(httpResponse.getEntity().getContent(),
                new TypeReference<>() {
        });

        //Because ID will change
        userDTO.setId(users.get(0).getId());
        assertEquals(new ModelMapper().map(users.get(0), UserDTO.class), userDTO);
    }

    //
}
