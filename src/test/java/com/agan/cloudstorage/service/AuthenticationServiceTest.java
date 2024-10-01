package com.agan.cloudstorage.service;

import com.agan.cloudstorage.exception.GeneralServiceException;
import com.agan.cloudstorage.exception.InvalidInputException;
import com.agan.cloudstorage.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private User user;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should authenticate successfully with correct credentials")
    void shouldAuthenticateSuccessfullyWithCorrectCredentials() {
        String username = "testuser";
        String password = "password123";
        UserDetails userDetails = User.withUsername(username)
                .password(password)
                .authorities(Collections.emptyList())
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        String token = authenticationService.authenticate(username, password);

        assertNotNull(token);
        assertEquals("mocked-jwt-token", token);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is incorrect")
    void shouldThrowBadCredentialsExceptionWhenPasswordIsIncorrect() {

        String username = "testuser";
        String wrongPassword = "wrongPassword";
        UserDetails userDetails = User.withUsername(username)
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(wrongPassword, userDetails.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(username, wrongPassword);
        });
    }

    @Test
    @DisplayName("Should throw InvalidInputException when user is not found")
    void shouldThrowInvalidInputExceptionWhenUserNotFound() {
        String username = "unknownUser";
        String password = "password123";

        when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(InvalidInputException.class, () -> {
            authenticationService.authenticate(username, password);
        });
    }

    @Test
    @DisplayName("Should throw GeneralServiceException when a general error occurs")
    void shouldThrowGeneralServiceExceptionWhenGeneralErrorOccurs() {
        String username = "testuser";
        String password = "password123";
        UserDetails userDetails = User.withUsername(username)
                .password(password)
                .authorities(Collections.emptyList())
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(GeneralServiceException.class, () -> {
            authenticationService.authenticate(username, password);
        });
    }
}

