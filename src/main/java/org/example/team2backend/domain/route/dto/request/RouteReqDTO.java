package org.example.team2backend.domain.route.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class RouteReqDTO {
    public record PlaceDTO(
            String placeName,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            String description,
            Boolean isActivate
    ) {}
    public record CreateRouteDTO(
            String routeName,
            List<PlaceDTO> places
    ) {}
}
