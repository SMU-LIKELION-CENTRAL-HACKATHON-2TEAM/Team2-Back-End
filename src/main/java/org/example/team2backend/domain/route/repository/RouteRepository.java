package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
