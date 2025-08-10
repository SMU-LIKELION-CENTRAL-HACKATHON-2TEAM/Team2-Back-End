package org.example.team2backend.domain.review.converter;

import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;


public class ReviewConverter {

    public static Review toReview(String content) {
        return Review.builder()
                .content(content)
                .build();
    }

    public static ReviewImage toReviewImage(String imageKey, String imageUrl, Review review) {
        return ReviewImage.builder()
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .review(review)
                .build();
    }

    public static ReviewResponseDTO.ReviewCreateResDTO toReviewCreateResDTO(Review review) {
        return ReviewResponseDTO.ReviewCreateResDTO.builder()
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
