package org.example.team2backend.domain.member.repository;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.route.entity.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRouteRepository extends JpaRepository<MemberRoute, Long> {

    // 회원의 전체 스크랩
    List<MemberRoute> findMemberRouteByMember(Member member);

    // 회원 + 특정 루트 (중복 방지 체크용)
    Optional<MemberRoute> findMemberRouteByMemberAndRoute(Member member, Route route);

    // 📌 커서 기반 페이지네이션 추가
    Slice<MemberRoute> findByMemberAndIdLessThanOrderByIdDesc(
            Member member, Long cursorId, Pageable pageable
    );
}
