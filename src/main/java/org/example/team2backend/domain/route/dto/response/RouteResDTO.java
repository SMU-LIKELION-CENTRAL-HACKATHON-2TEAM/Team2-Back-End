package org.example.team2backend.domain.route.dto.response;

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

    public record RouteDTO(
            Long routeId,
            String name,
            PlaceDTO startPlace,
            List<PlaceDTO> places,
            String summary
    ) {}

    // 📌 커서 기반 응답 DTO
    public record CursorResDTO<T>(
            List<T> content,
            boolean hasNext,
            Long nextCursor
    ) {}



}
