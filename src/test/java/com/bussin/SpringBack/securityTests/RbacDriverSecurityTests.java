package com.bussin.SpringBack.securityTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContextConfig.class, H2JpaConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RbacDriverSecurityTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CognitoLogin cognitoLogin;

    private String idToken;

    private static final String AUTHORIZATION_HEADER = "Authorization";


    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(true);
        driverService.addNewDriver(userService.createNewUser(
                TestObjects.COGNITO_DRIVER_DTO).getId(), TestObjects.DRIVER_DTO);
    }

    @Test
    public void getDriverByCarPlate_authorized_200() throws IOException {
        HttpUriRequest request = new HttpGet(baseUrl + port + "/api/v1" +
                "/driver/" + TestObjects.DRIVER_DTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(200, httpResponse.getCode());
    }
}
