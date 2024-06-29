package com.agan.cloudstorage.service;

import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class FileService {

    private FileRepository fileRepository;


    public List<File> getFilesByUserId(String userId) {
        return fileRepository.findByUserId(userId);
    }

    public File uploadFile(File file) {
        return fileRepository.save(file);
    }

    public void deleteFile(String id) {
        fileRepository.deleteById(id);
    }

}
