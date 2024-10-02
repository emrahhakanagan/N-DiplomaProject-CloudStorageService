package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.exception.InvalidInputException;
import org.springframework.http.HttpHeaders;
import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    private String authToken;
    private String username;

    @BeforeEach
    void setUp() {
        authToken = "mocked-auth-token";
        username = "testuser";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null));
    }

    @Test
    @DisplayName("Should upload file successfully with correct parameters")
    void shouldUploadFileSuccessfully() {
        String filename = "testfile.txt";
        MultipartFile file = Mockito.mock(MultipartFile.class);

        ResponseEntity<Void> response = fileController.uploadFile(authToken, filename, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(fileService, Mockito.times(1)).uploadFile(username, filename, file);
    }

    @Test
    @DisplayName("Should list files successfully with valid parameters")
    void shouldListFilesSuccessfully() {
        int limit = 10;
        File file1 = new File();
        file1.setFilename("file1.txt");
        file1.setContent(new byte[0]);

        File file2 = new File();
        file2.setFilename("file2.txt");
        file2.setContent(new byte[0]);

        List<File> mockedFiles = Arrays.asList(file1, file2);

        when(fileService.listFiles(username, limit)).thenReturn(mockedFiles);

        ResponseEntity<List<File>> response = fileController.listFiles(authToken, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("file1.txt", response.getBody().get(0).getFilename());
        Mockito.verify(fileService, Mockito.times(1)).listFiles(username, limit);
    }

    @Test
    @DisplayName("Should download file successfully with valid parameters")
    void shouldDownloadFileSuccessfully() {
        String filename = "file1.txt";

        File mockedFile = new File();
        mockedFile.setFilename(filename);
        mockedFile.setContent(new byte[]{1, 2, 3});

        when(fileService.downloadFile(username, filename)).thenReturn(mockedFile);

        ResponseEntity<byte[]> response = fileController.downloadFile(authToken, filename);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockedFile.getContent().length, response.getBody().length);
        assertEquals("attachment; filename=\"" + mockedFile.getFilename() + "\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        Mockito.verify(fileService, Mockito.times(1)).downloadFile(username, filename);
    }

    @Test
    @DisplayName("Should throw InvalidInputException when filename is empty during download")
    void shouldThrowInvalidInputExceptionWhenFilenameIsEmptyDuringDownload() {
        String filename = "";

        when(fileService.downloadFile(username, filename)).thenThrow(new InvalidInputException("Filename cannot be empty."));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            fileController.downloadFile(authToken, filename);
        });

        assertEquals("Filename cannot be empty.", exception.getMessage());
    }


    @Test
    @DisplayName("Should throw InvalidInputException when filename is empty during upload")
    void shouldThrowInvalidInputExceptionWhenFilenameIsEmptyDuringUpload() {
        String authToken = "mocked-auth-token";
        String filename = "";
        MultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "Some content".getBytes());

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", null));

        Mockito.doThrow(new InvalidInputException("Filename cannot be empty."))
                .when(fileService).uploadFile("testuser", filename, file);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            fileController.uploadFile(authToken, filename, file);
        });
        assertEquals("Filename cannot be empty.", exception.getMessage());
    }

}
