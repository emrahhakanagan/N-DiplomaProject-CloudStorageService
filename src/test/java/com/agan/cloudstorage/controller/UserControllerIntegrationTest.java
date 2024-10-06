package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired)) // Используем конструктор для внедрения зависимостей
public class UserControllerIntegrationTest {

    private final UserController userController; // Поле отмечено как final для безопасного внедрения

    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "testpassword";

    @BeforeEach
    void setUp() {
        try {
            userController.getUser(TEST_USERNAME).getBody();
            userController.deleteUser(TEST_USERNAME);
        } catch (Exception ignored) {
        }

        User testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword(TEST_PASSWORD);
        userController.addUser(testUser);
    }

    @AfterEach
    void cleanUp() {
        try {
            userController.deleteUser(TEST_USERNAME);
        } catch (Exception ignored) {
        }
    }

    @Test
    void shouldAddUserSuccessfully() {
        ResponseEntity<User> response = userController.getUser(TEST_USERNAME);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(TEST_USERNAME, Objects.requireNonNull(response.getBody()).getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        User duplicateUser = new User();
        duplicateUser.setUsername(TEST_USERNAME);
        duplicateUser.setPassword("newpassword");

        assertThrows(IllegalArgumentException.class, () -> {
            userController.addUser(duplicateUser);
        });
    }
}