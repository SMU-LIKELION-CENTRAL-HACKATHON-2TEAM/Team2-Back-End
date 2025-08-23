package org.example.team2backend.domain.place.dto.request;

public class PlaceReqDTO {
    public record UpdateReqDTO(
            String name,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            Boolean isActive
    ) {}
}
