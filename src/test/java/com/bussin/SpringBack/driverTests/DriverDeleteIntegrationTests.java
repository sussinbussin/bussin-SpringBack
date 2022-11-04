package com.bussin.SpringBack.driverTests;

import com.bussin.SpringBack.TestObjects;
import com.bussin.SpringBack.integrationTestAuth.CognitoLogin;
import com.bussin.SpringBack.models.driver.Driver;
import com.bussin.SpringBack.models.driver.DriverDTO;
import com.bussin.SpringBack.models.user.User;
import com.bussin.SpringBack.services.DriverService;
import com.bussin.SpringBack.services.UserService;
import com.bussin.SpringBack.testConfig.H2JpaConfig;
import com.bussin.SpringBack.testConfig.TestContextConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
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
public class DriverDeleteIntegrationTests {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @Autowired
    private CognitoLogin cognitoLogin;

    private String idToken;

    private User user;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Authenticate JWTToken and create a new TestObject user before each tests
     */
    @BeforeEach
    private void setUp() throws IOException {
        idToken = "Bearer " + cognitoLogin.getAuthToken(true);
        user = userService.createNewUser(TestObjects.COGNITO_DRIVER_DTO);
    }

    /**
     * Delete a driver when car plate exist success
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void deleteDriver_success() throws IOException {
        DriverDTO driverDTO = TestObjects.DRIVER_DTO.clone();

        Driver driver = driverService.addNewDriver(user.getId(), driverDTO);

        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/driver/" + driverDTO.getCarPlate());
        request.setHeader(AUTHORIZATION_HEADER, idToken);

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

    /**
     * Delete a driver when car plate is not found throw 404 NOT_FOUND
     * @throws IOException If an input or output exception occurred
     */
    @Test
    public void deleteDriver_noDriver_404() throws IOException {
        HttpUriRequest request = new HttpDelete(baseUrl + port
                + "/api/v1/driver/" + "SSS13370Z");
        request.setHeader(AUTHORIZATION_HEADER, idToken);

        CloseableHttpResponse httpResponse =
                HttpClientBuilder.create().build().execute(request);

        assertEquals(404, httpResponse.getCode());
    }
}
