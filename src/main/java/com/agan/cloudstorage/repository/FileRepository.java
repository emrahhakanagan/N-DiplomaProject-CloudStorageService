package com.agan.cloudstorage.repository;


import com.agan.cloudstorage.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByUserId(String userId);

    Optional<File> findByIdAndUserId(String fileId, String userId);

    void deleteByIdAndUserId(String id, String userId);

}
