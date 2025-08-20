package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRouteRepository extends JpaRepository<MemberRoute, Long> {

    List<MemberRoute> findMemberRouteByMember(Member member);

    Optional<MemberRoute> findMemberRouteByMemberAndRoute(Member member, Route route);
}
