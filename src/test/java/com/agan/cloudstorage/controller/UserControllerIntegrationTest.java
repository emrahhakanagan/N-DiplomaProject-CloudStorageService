package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.User;
import com.agan.cloudstorage.repository.UserRepository;
import com.agan.cloudstorage.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserControllerIntegrationTest {

    // Создаем контейнер MongoDB
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @BeforeAll
    static void setUp() {
        // Запускаем контейнер и устанавливаем URL для подключения к MongoDB
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    static void tearDown() {
        // Останавливаем контейнер после выполнения всех тестов
        mongoDBContainer.stop();
    }

    @Test
    @DisplayName("Should add user successfully with valid data")
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        ResponseEntity<String> response = userController.addUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User added successfully", response.getBody());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("password123");

        // Добавляем пользователя в репозиторий
        userService.addUser(user);

        // Проверяем, что исключение будет выброшено при повторной попытке добавить того же пользователя
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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Password must contain at least 6 characters", exception.getMessage());
    }
}
