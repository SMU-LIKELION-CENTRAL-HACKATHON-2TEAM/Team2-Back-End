package org.example.team2backend.domain.review.converter;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;


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
                        .memberId(review.getMember().getId())
                        .nickname(review.getMember().getNickname())
                        .build())
                .build();
    }

    public static ReviewLike toReviewLike(Review review, Member member) {
        return ReviewLike.builder()
                .review(review)
                .member(member)
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
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO> toReviewSliceResponse(
            Slice<Review> slice,
            Map<Long, List<ReviewImage>> imageMap
    ) {
        List<ReviewResponseDTO.ReviewResDTO> dtos = slice.getContent().stream()
                .map(review -> toReviewResDTO(review, imageMap.getOrDefault(review.getId(), List.of())))
                .toList();

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getNumberOfElements() - 1).getId()
                : null;

        return ReviewResponseDTO.CursorResDTO.<ReviewResponseDTO.ReviewResDTO>builder()
                .content(dtos)
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    public static ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.MyReviewResDTO> toMyReviewSliceResponse(
            Slice<Review> slice,
            Map<Long, List<ReviewImage>> imageMap
    ) {
        List<ReviewResponseDTO.MyReviewResDTO> dtos = slice.getContent().stream()
                .map(review -> toMyReviewResDTO(review, imageMap.getOrDefault(review.getId(), List.of())))
                .toList();

        Long nextCursor = slice.hasNext()
                ? slice.getContent().get(slice.getNumberOfElements() - 1).getId()
                : null;

        return ReviewResponseDTO.CursorResDTO.<ReviewResponseDTO.MyReviewResDTO>builder()
                .content(dtos)
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }
}
