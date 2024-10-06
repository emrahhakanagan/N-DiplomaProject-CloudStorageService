package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.AuthenticationRequest;
import com.agan.cloudstorage.model.AuthenticationResponse;
import com.agan.cloudstorage.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class AuthControllerIntegrationTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    private final UserController userController;
    private final AuthController authController;

    @BeforeAll
    static void setUpDB() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", "mongodb://localhost:27017/cloud_storage");
    }

    @AfterAll
    static void tearDownDB() {
        mongoDBContainer.stop();
    }

    @BeforeEach
    void setUp() {
        try {
            userController.deleteUser("testuser");
        } catch (Exception ignored) {
        }

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
        userController.addUser(testUser);
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT token")
    void shouldAuthenticateUserSuccessfully() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setLogin("testuser");
        request.setPassword("testpassword");

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        String jwtToken = ((AuthenticationResponse) response.getBody()).getJwt();
        assertNotNull(jwtToken);

        assertFalse(jwtToken.isEmpty());

        assertEquals(jwtToken, response.getHeaders().getFirst("auth-token"));
    }

    @Test
    @DisplayName("Should successfully log out user")
    void shouldLogoutUserSuccessfully() {
        String authToken = "mocked-auth-token";

        ResponseEntity<String> response = authController.logout(authToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully logged out", response.getBody());
    }
}
