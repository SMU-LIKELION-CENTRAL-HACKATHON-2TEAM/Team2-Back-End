package org.example.team2backend.domain.place.dto.request;

import java.util.List;

public class PlaceReqDTO {
    public record UpdateReqDTO(
            String name,
            String category,
            String address,
            String kakaoId,
            Double lat,
            Double lng,
            Boolean isActive,
            List<String> imageUrls
    ) {}
}
