package org.example.team2backend.domain.route.service.query;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.entity.RoutePlace;
import org.example.team2backend.domain.route.repository.RoutePlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RouteQueryService {

    private final RoutePlaceRepository routePlaceRepository;

    //리뷰 단건 조회
    public List<Place> getPlacesByRouteId(Long routeId) {
        List<RoutePlace> routePlaces = routePlaceRepository.findAllByRouteId(routeId);

        //RoutePlace에서 Place만 뽑아서 반환
        return routePlaces.stream()
                .map(RoutePlace::getPlace)
                .toList();
    }
}
