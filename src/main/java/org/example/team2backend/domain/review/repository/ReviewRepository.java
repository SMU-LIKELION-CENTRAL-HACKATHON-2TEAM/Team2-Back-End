package org.example.team2backend.domain.review.repository;


import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRoute(Route route);
    List<Review> findByUser(User user);

    Slice<Review> findByRouteAndIdLessThanOrderByIdDesc(Route route, Long id, Pageable pageable);
    Slice<Review> findByUserAndIdLessThanOrderByIdDesc(User user, Long id, Pageable pageable);
}
