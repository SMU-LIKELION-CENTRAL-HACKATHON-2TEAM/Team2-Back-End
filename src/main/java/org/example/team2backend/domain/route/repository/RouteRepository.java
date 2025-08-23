package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query("SELECT DISTINCT r FROM Route r " +
            "WHERE EXISTS (SELECT 1 FROM Review rev WHERE rev.route = r AND rev.member = :member) " +
            "AND r.id < :cursor " +
            "ORDER BY r.id DESC")
    Slice<Route> findDistinctRoutesWithReviewsByMemberAndIdLessThanOrderByIdDesc(
            @Param("member") Member member,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Modifying
    @Query("update Route r set r.visitCount = r.visitCount + 1 where r.id = :routeId")
    void increaseVisitCount(@Param("routeId") Long routeId);

    // bookmarked +1
    @Modifying
    @Query("update Route r set r.bookmarked = r.bookmarked + 1 where r.id = :routeId")
    void increaseBookmarked(@Param("routeId") Long routeId);

    // bookmarked -1
    @Modifying
    @Query("update Route r set r.bookmarked = r.bookmarked - 1 where r.id = :routeId and r.bookmarked > 0")
    void decreaseBookmarked(@Param("routeId") Long routeId);

    @Modifying
    @Query("update Route r set r.viewCount = r.viewCount + 1 where r.id = :routeId")
    void increaseViewCount(@Param("routeId") Long routeId);


}
