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
            Boolean isMine,
            Long likeCount,
            AuthorDTO author
    ) {}

    @Builder
    public record AuthorDTO(
            Long memberId,
            String nickname
    ) {}

    @Builder
    public record MyReviewResDTO(
            Long routeId,
            String name,
            String startPlace,
            //TODO : 루트 사진 ( 첫번째 장소 사진 ) 추가
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}


    @Builder
    public record CursorResDTO<T>(
            List<T> content,
            Boolean hasNext,
            Long nextCursor
    ) {}
}
