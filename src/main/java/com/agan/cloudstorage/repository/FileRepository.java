package com.agan.cloudstorage.repository;

import com.agan.cloudstorage.model.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends MongoRepository<File, String> {

    @Query("{'userId': ?0}")
    List<File> findFilesByUserId(String userId, Pageable pageable);

    Optional<File> findByUserIdAndFilename(String userId, String filename);

    Optional<File> findByIdAndUserId(String fileId, String userId);

    void deleteByIdAndUserId(String id, String userId);

}
