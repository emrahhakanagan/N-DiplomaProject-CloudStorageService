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

@RequiredArgsConstructor
@Service
public class FileService {

    /*
     * The 401 (Unauthorized) error is handled automatically by JwtRequestFilter,
     * so we do not need to implement this logic in the editFileName method or the service.
     * This ensures that our code still meets the specification requirements for authentication.
     */

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

    public void editFileName(String userId, String filename, String newFileName) {
        try {
            if (newFileName == null || newFileName.trim().isEmpty()) {
                throw new InvalidInputException("New file name cannot be empty.");
            }

            File file = fileRepository.findByUserIdAndFilename(userId, filename)
                    .orElseThrow(() -> new FilesNotFoundException(
                            "File not found for user: " + userId + " with filename: " + filename)
                    );

            file.setFilename(newFileName);
            fileRepository.save(file);

        } catch (FilesNotFoundException | InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error while updating file name", e);
        }
    }

    public File downloadFile(String userId, String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                throw new InvalidInputException("Filename cannot be empty.");
            }

            File file = fileRepository.findByUserIdAndFilename(userId, filename)
                    .orElseThrow(() -> new FilesNotFoundException(
                            "File not found for user: " + userId + " with filename: " + filename)
                    );

            return file;

        } catch (InvalidInputException | FilesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error while downloading file", e);
        }
    }

    public void deleteFile(String userId, String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                throw new InvalidInputException("Filename cannot be empty.");
            }

            File file = fileRepository.findByUserIdAndFilename(userId, filename)
                    .orElseThrow(() -> new FilesNotFoundException(
                            "File not found for user: " + userId + " with filename: " + filename)
                    );

            fileRepository.delete(file);

        } catch (InvalidInputException | FilesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error while deleting file", e);
        }
    }

    public void uploadFile(String userId, String filename, MultipartFile file) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                throw new InvalidInputException("Filename cannot be empty.");
            }

            if (file == null || file.isEmpty()) {
                throw new InvalidInputException("File cannot be empty.");
            }

            File dbFile = createFileObject(userId, filename, file);

            fileRepository.save(dbFile);

        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralServiceException("Error while uploading file", e);
        }
    }

    private File createFileObject(String userId, String filename, MultipartFile file) throws IOException {
        File dbFile = new File();
        dbFile.setUserId(userId);
        dbFile.setFilename(filename);
        dbFile.setSize(file.getSize());
        dbFile.setContent(file.getBytes());
        return dbFile;
    }
}
