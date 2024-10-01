package com.agan.cloudstorage.service;

import com.agan.cloudstorage.exception.FilesNotFoundException;
import com.agan.cloudstorage.exception.InvalidInputException;
import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.repository.FileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("Should upload file successfully")
    void shouldUploadFileSuccessfully() {
        String userId = "testUser";
        String fileName = "testFile.txt";
        MultipartFile file = new MockMultipartFile("file", "testFile.txt"
                , "text/plain", "Test content".getBytes());


        assertDoesNotThrow(() -> fileService.uploadFile(userId, fileName, file));
    }

    @Test
    @DisplayName("Should list files successfully for a valid user and limit")
    void shouldListFilesSuccessfully() {
        String userId = "testUser";
        int limit = 5;
        File file = new File();
        file.setUserId(userId);
        file.setFilename("testFile.txt");
        List<File> files = Collections.singletonList(file);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        when(fileRepository.findFilesByUserId(userId, pageable)).thenReturn(files);

        List<File> result = fileService.listFiles(userId, limit);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testFile.txt", result.get(0).getFilename());
    }

    @Test
    @DisplayName("Should throw InvalidInputException when limit is less than or equal to zero")
    void shouldThrowInvalidInputExceptionWhenLimitIsInvalid() {
        String userId = "testUser";
        int limit = 0;

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            fileService.listFiles(userId, limit);
        });

        assertEquals("Limit must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw FilesNotFoundException when no files are found")
    void shouldThrowFilesNotFoundExceptionWhenNoFilesFound() {
        String userId = "testUser";
        int limit = 5;

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        when(fileRepository.findFilesByUserId(userId, pageable)).thenReturn(Collections.emptyList());

        FilesNotFoundException exception = assertThrows(FilesNotFoundException.class, () -> {
            fileService.listFiles(userId, limit);
        });

        assertEquals("No files found", exception.getMessage());
    }

}
