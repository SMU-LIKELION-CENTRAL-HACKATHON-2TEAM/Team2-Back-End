package org.example.team2backend.domain.review.converter;

import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.example.team2backend.domain.user.entity.User;

import java.util.List;


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

    public static ReviewResponseDTO.ReviewResDTO toReviewResDTO(Review review, List<ReviewImage> reviewImages) {
        return ReviewResponseDTO.ReviewResDTO.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .images(reviewImages.stream()
                        .map(ReviewImage::getImageUrl)
                        .toList())
                .createdAt(review.getCreatedAt())
                .author(ReviewResponseDTO.AuthorDTO.builder()
                        .userId(review.getUser().getId())
                        .nickname(review.getUser().getNickname())
                        .build())
                .build();
    }

    public static ReviewLike toReviewLike(Review review, User user) {
        return ReviewLike.builder()
                .review(review)
                .user(user)
                .build();
    }

    public static ReviewResponseDTO.MyReviewResDTO toMyReviewResDTO(Review review, List<ReviewImage> reviewImages) {
        return ReviewResponseDTO.MyReviewResDTO.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .images(reviewImages.stream()
                        .map(ReviewImage::getImageUrl)
                        .toList())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
