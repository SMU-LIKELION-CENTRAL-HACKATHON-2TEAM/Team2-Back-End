package org.example.team2backend.domain.place.repository;

import org.example.team2backend.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.lat = :lat")
    Optional<Place> findByLatitude(@Param("lat") Double lat);

    @Query("SELECT p FROM Place p WHERE p.lng = :lng")
    Optional<Place> findByLongitude(@Param("lng") Double lng);

    @Query("SELECT p FROM Place p WHERE p.kakaoId = :kakaoId")
    Optional<Place> findByKakaoId(@Param("kakaoId") String kakaoId);


}
