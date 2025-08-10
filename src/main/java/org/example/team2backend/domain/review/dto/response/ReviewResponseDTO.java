package org.example.team2backend.domain.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;


public class ReviewResponseDTO {

    @Builder
    public record ReviewCreateResDTO(
        Long reviewId,
        LocalDateTime createdAt
    ){}




}
