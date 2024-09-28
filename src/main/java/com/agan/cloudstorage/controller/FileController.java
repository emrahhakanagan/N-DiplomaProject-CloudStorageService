package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FileController {
/*
* Getting the auth-token from the header is required by the specification, but we do not use this variable directly.
* The authentication process is automatically handled by Spring Security through JwtRequestFilter.
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
