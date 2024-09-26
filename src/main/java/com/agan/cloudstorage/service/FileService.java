package com.agan.cloudstorage.service;

import com.agan.cloudstorage.exception.FilesNotFoundException;
import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileService {

    private FileRepository fileRepository;


    public List<File> listFiles(String userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));

        List<File> files = fileRepository.findFilesByUserId(userId, pageable);

        if (files == null || files.isEmpty()) {
            throw new FilesNotFoundException("Files not found for user with ID: " + userId);
        }

        return files;
    }

    public File uploadFile(MultipartFile file, String userId) throws IOException {
        File dbFile = new File();
        dbFile.setFilename(file.getOriginalFilename());
        dbFile.setUserId(userId);
        dbFile.setSize(file.getSize());
        dbFile.setContent(file.getBytes());

        return fileRepository.save(dbFile);
    }

    public Optional<File> downloadFile(String fileId, String userId) {
        return fileRepository.findByIdAndUserId(fileId, userId);
    }

    public void deleteFile(String fileId, String userId) {
        fileRepository.deleteByIdAndUserId(fileId, userId);
    }

}
