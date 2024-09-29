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

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FileController {
    /*
     * Getting the auth-token from the header is required by the specification, but we do not use this variable directly.
     * The authentication process is automatically handled by Spring Security through JwtRequestFilter.
     *
     * authToken The authentication token provided in the request header.
     */

    /*
     * It's generally better to use fileId for CRUD operations to ensure accuracy and security,
     * as fileId is a unique identifier. However, the specification strictly requires the use
     * of filename as a parameter for file operations. Therefore, we are accepting filename in
     * the parameters to comply with the specification.
     *
     * filename The name of the file to be used for the operation, as required by the specification.
     */

    private final FileService fileService;

    @GetMapping("/list")
    public ResponseEntity<List<File>> listFiles(
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<File> files = fileService.listFiles(username, limit);

        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestParam("filename") String filename,
            @RequestBody Map<String, String> requestBody) {

        String newFileName = requestBody.get("name");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        fileService.editFileName(username, filename, newFileName);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestParam("filename") String filename) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        File file = fileService.downloadFile(username, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestParam("filename") String filename) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        fileService.deleteFile(username, filename);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestParam("filename") String filename,
            @RequestPart("file") MultipartFile file) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        fileService.uploadFile(username, filename, file);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
