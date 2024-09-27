package com.agan.cloudstorage.service;

import com.agan.cloudstorage.exception.FilesNotFoundException;
import com.agan.cloudstorage.exception.GeneralServiceException;
import com.agan.cloudstorage.exception.InvalidInputException;
import com.agan.cloudstorage.model.File;
import com.agan.cloudstorage.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {

    private int pageNumber = 0;
    private FileRepository fileRepository;


    public List<File> listFiles(String userId, int limit) {
        try {
            if (limit <= 0) {
                throw new InvalidInputException("Limit must be greater than 0");
            }

            Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "uploadedAt"));

            List<File> files = fileRepository.findFilesByUserId(userId, pageable);

            if (files == null || files.isEmpty()) {
                throw new FilesNotFoundException("No files found");
            }

            return files;
        } catch (InvalidInputException | FilesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error getting file list for user with ID: " + userId, e);
        }
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
