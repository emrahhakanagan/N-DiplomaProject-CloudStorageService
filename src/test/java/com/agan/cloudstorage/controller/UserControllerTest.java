package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.User;
import com.agan.cloudstorage.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Should add user successfully with valid data")
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        doNothing().when(userService).addUser(user);

        ResponseEntity<String> response = userController.addUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added successfully", response.getBody());
        Mockito.verify(userService, Mockito.times(1)).addUser(user);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("password123");

        Mockito.doThrow(new IllegalArgumentException("A user with this username already exists"))
                .when(userService).addUser(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("A user with this username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password is less than 6 characters")
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("123");

        Mockito.doThrow(new IllegalArgumentException("Password must contain at least 6 characters"))
                .when(userService).addUser(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Password must contain at least 6 characters", exception.getMessage());
    }

    

}
