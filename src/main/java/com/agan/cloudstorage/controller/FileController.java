package com.agan.cloudstorage.controller;

import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/cloud")
public class FileController {

    private FileService fileService;

    @GetMapping("/list")
    public List<File> listFiles(@RequestParam String userId) {
        return fileService.getFilesByUserId(userId);
    }

    @PostMapping("/file")
    public File uploadFile(@RequestBody File file) {
        return fileService.uploadFile(file);
    }

    @GetMapping("/file")
    public void deleteFile(@RequestParam String id) {
        fileService.deleteFile(id);
    }


}
