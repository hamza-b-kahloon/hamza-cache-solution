package com.hamza.natwest.hamzacachesolution.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheObjectRepository<T> extends MongoRepository<Integer, String> {
    Optional<Integer> findById(String id);
}
