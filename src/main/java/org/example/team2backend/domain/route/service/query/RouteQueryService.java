package org.example.team2backend.domain.route.service.query;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RouteQueryService {

    private final RoutePlaceRepository routePlaceRepository;

    //루트 단건 조회
    /*public List<Place> getPlacesByRouteId(Long kakaoId) {
        //카카오 아이디를 기반으로
        List<RoutePlace> routePlaces = routePlaceRepository.findAllByRouteId(kakaoId);

        //RoutePlace에서 Place만 뽑아서 반환
        return routePlaces.stream()
                .map(RoutePlace::getPlace)
                .toList();
    }*/
}
