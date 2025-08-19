package org.example.team2backend.domain.route.dto.response;

import org.example.team2backend.domain.route.dto.request.RouteReqDTO;

import java.util.List;

public class RouteResDTO {

    public record PlaceDTO(
            Long placeId,
            String placeName,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            String description,
            Boolean isActivate
    ) {}

    public record GetRouteDTO(
            String routeName,
            List<RouteReqDTO.PlaceDTO> places
    ) {}
}
