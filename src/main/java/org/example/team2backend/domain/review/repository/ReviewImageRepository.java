package org.example.team2backend.domain.review.repository;

import org.example.team2backend.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
