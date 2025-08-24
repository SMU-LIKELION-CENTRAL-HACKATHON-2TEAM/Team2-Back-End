package org.example.team2backend.domain.review.converter;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.example.team2backend.domain.route.entity.Route;
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

    public static ReviewResponseDTO.ReviewResDTO toReviewResDTO(Review review, List<ReviewImage> reviewImages, Long currentMemberId, Long likeCount) {
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
                .isMine(currentMemberId != null && currentMemberId.equals(review.getMember().getId()))
                .likeCount(likeCount)
                .build();
    }

    public static ReviewLike toReviewLike(Review review, Member member) {
        return ReviewLike.builder()
                .review(review)
                .member(member)
                .build();
    }

    public static ReviewResponseDTO.MyReviewResDTO toMyReviewResDTO(Route route, String startPlace, String imageUrl) {
        return ReviewResponseDTO.MyReviewResDTO.builder()
                .routeId(route.getId())
                .name(route.getName())
                .startPlace(startPlace)
                .imageUrl(imageUrl)
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }

    public static ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO> toReviewSliceResponse(
            Slice<Review> slice,
            Map<Long, List<ReviewImage>> imageMap,
            Map<Long, Long> likeCountMap,
            Long currentMemberId
    ) {
        List<ReviewResponseDTO.ReviewResDTO> dtos = slice.getContent().stream()
                .map(review -> toReviewResDTO(review, imageMap.getOrDefault(review.getId(), List.of()), currentMemberId, likeCountMap.getOrDefault(review.getId(), 0L)))
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
            Slice<Route> slice,
            Map<Long, ReviewResponseDTO.StartPlaceInfo> firstPlaceMap
    ) {
        List<ReviewResponseDTO.MyReviewResDTO> dtos = slice.getContent().stream()
                .map(route -> {
                    ReviewResponseDTO.StartPlaceInfo info = firstPlaceMap.getOrDefault(route.getId(), new ReviewResponseDTO.StartPlaceInfo("장소 없음", null));
                    return toMyReviewResDTO(route, info.name(), info.imageUrl());
                })
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
