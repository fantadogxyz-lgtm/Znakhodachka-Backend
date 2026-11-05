package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Repository
public interface GeolocationRepository extends JpaRepository<Geolocation, UUID> {
}
