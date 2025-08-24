package org.example.team2backend.domain.place.repository;

import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    void deleteByPlace(Place place);

    Optional<PlaceImage> findFirstByPlace(Place place);
}
