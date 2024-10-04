package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.AuthenticationRequest;
import com.agan.cloudstorage.model.AuthenticationResponse;
import com.agan.cloudstorage.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AuthControllerIntegrationTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    private AuthController authController;
    private AuthenticationService authenticationService;

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT token")
    void shouldAuthenticateUserSuccessfully() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setLogin("testuser");
        request.setPassword("password123");

        when(authenticationService.authenticate(request.getLogin(), request.getPassword()))
                .thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mocked-jwt-token", ((AuthenticationResponse) response.getBody()).getJwt());
        assertEquals("mocked-jwt-token", response.getHeaders().getFirst("auth-token"));
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
