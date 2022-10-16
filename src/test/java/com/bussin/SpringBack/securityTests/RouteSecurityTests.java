package com.bussin.SpringBack.securityTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.User;
import com.bussin.SpringBack.models.UserDTO;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class RouteSecurityTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Test
    public void getUserById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users/"
                + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    @Test
    public void createUserWCognito_invalid_400() throws IOException {
        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1/users" +
                "/wCognito/create");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    @Test
    public void getDriverByCarPlate_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/" + TestObjects.DRIVER.getCarPlate());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    @Test
    public void getPlannedRouteById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    @Test
    public void getRideById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }
}
