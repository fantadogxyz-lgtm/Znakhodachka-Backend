package com.znahodochka.backend.storage.repositories;

import com.znahodochka.backend.storage.model.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BucketRepository extends JpaRepository<Bucket, UUID> {
    boolean existsByOwnerId(Long ownerId);

    Optional<Bucket> findByOwnerId(Long ownerId);
}
