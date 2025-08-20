package org.example.team2backend.domain.member.service.query;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.member.repository.MemberRouteRepository;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberRouteQueryService {

    private final MemberRepository memberRepository;
    private final MemberRouteRepository memberRouteRepository;
    private final RoutePlaceRepository routePlaceRepository;

    //스크랩 루트 조회
    public List<RouteResDTO.RouteDTO> getScrapList(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));

        // 1. 사용자가 외래키로 들어가있는 모든 매핑 테이블 조회 -> 리스트로 반환
        List<MemberRoute> mrs = memberRouteRepository.findMemberRouteByMember(member);

        // 2. 각 MemberRoute → RouteDTO 변환
        return mrs.stream()
                .map(MemberRoute::getRoute)
                .map(route -> {
                    // 🔹 routePlaces를 직접 조회해야 함 (엔티티 필드 접근 불가)
                    List<RoutePlace> routePlaces = routePlaceRepository.findByRoute(route);

                    List<RouteResDTO.PlaceDTO> places = routePlaces.stream()
                            .map(rp -> {
                                Place p = rp.getPlace();
                                return new RouteResDTO.PlaceDTO(
                                        p.getId(),
                                        p.getName(),
                                        p.getCategory(),
                                        p.getAddress(),
                                        p.getKakaoId(),
                                        p.getLat(),
                                        p.getLng(),
                                        p.getDescription(),
                                        p.getIsActive()
                                );
                            })
                            .toList();

                    return new RouteResDTO.RouteDTO(
                            places.isEmpty() ? null : places.get(0), // startPlace
                            places.size() > 1 ? places.subList(1, places.size()) : List.of(),
                            route.getSummary()
                    );
                })
                .toList();
    }

}
