package org.example.team2backend.domain.review.repository;


import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRoute(Route route);

}
