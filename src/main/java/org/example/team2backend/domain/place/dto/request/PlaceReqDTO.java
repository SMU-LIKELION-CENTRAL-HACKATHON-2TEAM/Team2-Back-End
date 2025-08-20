package org.example.team2backend.domain.place.dto.request;

public class PlaceReqDTO {
    public record UpdateReqDTO(
            String placeName,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            String description,
            Boolean isActivate
    ) {}
}
