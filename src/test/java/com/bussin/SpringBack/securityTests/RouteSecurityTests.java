package com.bussin.SpringBack.securityTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class RouteSecurityTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    /**
     * Get user by id when user is unauthorized throws 401 UNAUTHORIZED
     * @throws IOException
     */
    @Test
    public void getUserById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1/users/"
                + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    /**
     * Create user with cognito with invalid parameters throws 400 BAD REQUEST
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void createUserWCognito_invalidParams_400() throws IOException {
        HttpUriRequest request = new HttpPost(baseUrl + port + "/api/v1/users" +
                "/wCognito/create");

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(400, httpResponse.getCode());
    }

    /**
     * Get driver by car plate when unauthorized throws 401 UNAUTHORIZED
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void getDriverByCarPlate_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port
                + "/api/v1/driver/" + TestObjects.DRIVER.getCarPlate());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    /**
     * Get planned route by id when unauthorized throws 401 UNAUTHORIZED
     * @throws IOException
     */
    @Test
    public void getPlannedRouteById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/planned/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }

    /**
     * Get ride by id when unauthorized throws 401 UNAUTHORIZED
     * @throws IOException
     */
    @Test
    public void getRideById_Unauthorized_401() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/ride/" + UUID.randomUUID());

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(401, httpResponse.getCode());
    }
}
