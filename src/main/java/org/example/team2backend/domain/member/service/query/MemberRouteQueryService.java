package org.example.team2backend.domain.member.service.query;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.member.repository.MemberRouteRepository;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    public RouteResDTO.CursorResDTO<RouteResDTO.RouteDTO> getScrapList(
            String email, Long cursor, int size
    ) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size);
        Long effectiveCursorId = (cursor == null || cursor <= 0)
                ? Long.MAX_VALUE
                : cursor;

        Slice<MemberRoute> scrapSlice =
                memberRouteRepository.findByMemberAndIdLessThanOrderByIdDesc(
                        member, effectiveCursorId, pageable
                );

        List<RouteResDTO.RouteDTO> routeDTOs = scrapSlice.getContent().stream()
                .map(MemberRoute::getRoute)
                .map(route -> {
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
                                        p.getIsActive()
                                );
                            })
                            .toList();

                    return new RouteResDTO.RouteDTO(
                            route.getId(),                       // routeId
                            route.getName(),                     // name
                            places.isEmpty() ? null : places.get(0), // startPlace
                            places.size() > 1 ? places.subList(1, places.size()) : List.of(), // 나머지 장소들
                            route.getSummary()                   // summary
                    );

                })
                .toList();

        Long nextCursor = scrapSlice.hasNext()
                ? scrapSlice.getContent().get(scrapSlice.getContent().size() - 1).getId()
                : null;

        return new RouteResDTO.CursorResDTO<>(routeDTOs, scrapSlice.hasNext(), nextCursor);
    }
}
