package org.example.team2backend.domain.review.dto.request;

public class ReviewRequestDTO {

    public record CreateReviewRequestDTO(
            String content
    ){
    }
}
