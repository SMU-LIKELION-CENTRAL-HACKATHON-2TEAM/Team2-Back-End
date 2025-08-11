package org.example.team2backend.domain.review.repository;

import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.example.team2backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
    void deleteByReview(Review review);
}
