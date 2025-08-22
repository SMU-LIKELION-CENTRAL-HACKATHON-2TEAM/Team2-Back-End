package org.example.team2backend.domain.place.repository;

import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.place.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
    void deleteByPlace(Place place);
}
