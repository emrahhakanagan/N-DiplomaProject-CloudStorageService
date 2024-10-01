package com.agan.cloudstorage.service;

import com.agan.cloudstorage.model.User;
import com.agan.cloudstorage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void userSetup() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRoles(Collections.singletonList("USER"));
    }

    @Test
    @DisplayName("Should add user successfully when all conditions are met")
    void shouldAddUserSuccessfully() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        assertDoesNotThrow(() -> userService.addUser(user));

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPassword());
        assertTrue(user.getRoles().contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user with the same username already exists")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(user);
        });

        assertEquals("A user with this username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password is invalid")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        user.setPassword("123");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(user);
        });

        assertEquals("Password must contain at least 6 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should set default role to ROLE_USER when roles are not provided")
    void shouldSetDefaultRoleWhenRolesAreNotProvided() {
        user.setRoles(null);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.addUser(user);

        assertTrue(user.getRoles().contains("ROLE_USER"));
        verify(userRepository, times(1)).save(user);
    }
}
