package org.example.team2backend.domain.route.dto.request;

import java.util.List;

public class RouteReqDTO {
    public record PlaceDTO(
            String placeName,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            Boolean isActive
    ) {}
    public record CreateRouteDTO(
            String routeName,
            List<PlaceDTO> places
    ) {}

    public record ScrapRouteDTO(
            Long id
    ) {}
}
