package org.example.team2backend.domain.route.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.entity.RouteLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteLikeRepository extends JpaRepository<RouteLike, Long> {

    Optional<RouteLike> findByRouteAndMember(Route route, Member member);
}
