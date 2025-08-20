package org.example.team2backend.domain.review.repository;


import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRoute(Route route);
    List<Review> findByMember(Member user);

    Slice<Review> findByRouteAndIdLessThanOrderByIdDesc(Route route, Long id, Pageable pageable);
    Slice<Review> findByMemberAndIdLessThanOrderByIdDesc(Member member, Long id, Pageable pageable);

    List<Review> findByRouteAndMemberIdOrderByIdDesc(Route route, Long memberId);

    Slice<Review> findByRouteAndMemberIdNotAndIdLessThanOrderByIdDesc(
            Route route, Long memberId, Long cursorId, Pageable pageable);
}
