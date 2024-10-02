package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.exception.InvalidInputException;
import com.agan.cloudstorage.model.AuthenticationRequest;
import com.agan.cloudstorage.model.AuthenticationResponse;
import com.agan.cloudstorage.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Should authenticate successfully with correct credentials")
    void shouldAuthenticateSuccessfully() {
        String username = "testuser";
        String password = "password123";
        String expectedJwt = "mocked-jwt-token";

        AuthenticationRequest request = new AuthenticationRequest(username, password);

        when(authenticationService.authenticate(username, password)).thenReturn(expectedJwt);

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedJwt, response.getHeaders().getFirst("auth-token"));
        assertEquals(new AuthenticationResponse(expectedJwt), response.getBody());
    }

    @Test
    @DisplayName("Should throw exception when authentication fails with incorrect credentials")
    void shouldThrowExceptionWhenAuthenticationFails() {
        String username = "testuser";
        String password = "wrongpassword";

        AuthenticationRequest request = new AuthenticationRequest(username, password);

        when(authenticationService.authenticate(username, password))
                .thenThrow(new RuntimeException("Authentication failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.createAuthenticationToken(request);
        });

        assertEquals("Authentication failed", exception.getMessage());
    }

    @Test
    @DisplayName("Should log out successfully when auth token is provided")
    void shouldLogoutSuccessfully() {
        String authToken = "mocked-jwt-token";

        ResponseEntity<String> response = authController.logout(authToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully logged out", response.getBody());
    }

    @Test
    @DisplayName("Should return auth token on successful authentication")
    void shouldReturnAuthTokenOnSuccessfulAuthentication() {
        String login = "testuser";
        String password = "password123";
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(login, password);
        String mockedJwt = "mocked-jwt-token";

        when(authenticationService.authenticate(login, password)).thenReturn(mockedJwt);

        ResponseEntity<?> response = authController.createAuthenticationToken(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockedJwt, response.getHeaders().getFirst("auth-token"));
        assertTrue(response.getBody() instanceof AuthenticationResponse);
        assertEquals(mockedJwt, ((AuthenticationResponse) response.getBody()).getJwt());
    }

    @Test
    @DisplayName("Should throw InvalidInputException when authentication fails")
    void shouldThrowInvalidInputExceptionWhenAuthenticationFails() {
        String login = "invaliduser";
        String password = "wrongpassword";
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(login, password);

        when(authenticationService.authenticate(login, password))
                .thenThrow(new InvalidInputException("Invalid credentials"));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            authController.createAuthenticationToken(authenticationRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

}
