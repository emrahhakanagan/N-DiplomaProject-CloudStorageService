package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public ResponseEntity<List<File>> listFiles(
            @RequestHeader(value = "auth-token", required = true) String authToken, // Required by specification; token is processed in JwtRequestFilter
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<File> files = fileService.listFiles(username, limit);

        return new ResponseEntity<>(files, HttpStatus.OK);
    }


    @PostMapping("/file")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam String userId) throws IOException {
        File uploadedFile = fileService.uploadFile(file, userId);
        return new ResponseEntity<>(uploadedFile, HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileId, @RequestParam String userId) {
        return fileService.downloadFile(fileId, userId)
                .map(file -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                        .body(file.getContent()))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestParam String fileId, @RequestParam String userId) {
        fileService.deleteFile(fileId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
