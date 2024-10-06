package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class FileControllerIntegrationTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    private final FileController fileController;
    private final FileRepository fileRepository;

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
    void prepareTestData() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null)
        );


        fileRepository.deleteAll(fileRepository.findFilesByUserId("testuser", Pageable.unpaged()));

        MultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain"
                , "Test file content".getBytes());

        fileController.uploadFile("mocked-auth-token", "testfile.txt", file);
    }

    @AfterEach
    void cleanUp() {
        fileController.deleteFile("mocked-auth-token", "testfile.txt");

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should list files successfully")
    void shouldListFilesSuccessfully() {
        ResponseEntity<List<File>> response = fileController.listFiles("mocked-auth-token", 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should download file successfully")
    void shouldDownloadFileSuccessfully() {
        ResponseEntity<byte[]> response = fileController.downloadFile("mocked-auth-token", "testfile.txt");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test file content", new String(response.getBody()));
        assertEquals("attachment; filename=\"testfile.txt\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

}