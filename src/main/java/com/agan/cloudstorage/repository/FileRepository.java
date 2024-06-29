package com.agan.cloudstorage.repository;


import com.agan.cloudstorage.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {
    List<File> findByUserId(String userId);

}
