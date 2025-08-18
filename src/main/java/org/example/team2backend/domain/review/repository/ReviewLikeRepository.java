package org.example.team2backend.domain.review.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndMember(Review review, Member member);
    void deleteByReview(Review review);
    @Query("SELECT rl.review.id, COUNT(rl) " +
            "FROM ReviewLike rl " +
            "WHERE rl.review IN :reviews " +
            "GROUP BY rl.review.id")
    List<Object[]> countByReviewIn(@Param("reviews") List<Review> reviews);

    default Map<Long, Long> countMapByReviewIn(List<Review> reviews) {
        return countByReviewIn(reviews).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
}
