package org.example.team2backend.domain.route.converter;


import org.example.team2backend.domain.place.entity.Place;
import org.example.team2backend.domain.route.dto.request.RouteReqDTO;
import org.example.team2backend.domain.route.dto.response.RouteResDTO;
import org.example.team2backend.domain.route.entity.Route;

public class RouteConverter {

    public static Place toPlaceWithKakao(RouteReqDTO.PlaceDTO placeDTO) {
        return Place.builder()
                .name(placeDTO.placeName())
                .category(placeDTO.category())
                .address(placeDTO.address())
                .kakaoId(placeDTO.kakaoId())
                .lat(placeDTO.lat())
                .lng(placeDTO.lng())
                .isActive(placeDTO.isActivate())
                .description(placeDTO.description())
                .build();

    }

    public static Route toRoute(RouteReqDTO.CreateRouteDTO createRouteDTO) {
        //나머지 필드들은 null or 0으로 초기화
        return Route.builder()
                .name(createRouteDTO.routeName())
                .build();
    }

    public static RouteResDTO.PlaceDTO fromEntity(Place place) {
        return new RouteResDTO.PlaceDTO(
                place.getId(),
                place.getName(),
                place.getCategory(),
                place.getAddress(),
                place.getKakaoId(),
                place.getLat(),
                place.getLng(),
                place.getDescription(),
                place.getIsActive()
        );
    }

}
