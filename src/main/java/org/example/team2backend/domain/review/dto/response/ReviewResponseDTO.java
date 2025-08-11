package org.example.team2backend.domain.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


public class ReviewResponseDTO {

    @Builder
    public record ReviewCreateResDTO(
        Long reviewId,
        LocalDateTime createdAt
    ){}

    @Builder
    public record ReviewResDTO(
            Long reviewId,
            String content,
            List<String> images,
            LocalDateTime createdAt,
            AuthorDTO author
    ) {}

    @Builder
    public record AuthorDTO(
            Long userId,
            String nickname
    ) {}

    @Builder
    public record MyReviewResDTO(
            Long reviewId,
            String content,
            List<String> images,
            LocalDateTime createdAt
    ) {}

}
