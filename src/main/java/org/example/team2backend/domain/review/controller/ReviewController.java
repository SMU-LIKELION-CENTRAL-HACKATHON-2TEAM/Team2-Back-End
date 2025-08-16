package org.example.team2backend.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.service.command.ReviewCommandService;
import org.example.team2backend.domain.review.service.query.ReviewQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponse<ReviewResponseDTO.ReviewCreateResDTO> createReview(
            @RequestPart("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images){
        return CustomResponse.onSuccess(reviewCommandService.createReview(content, images));
    }

    @GetMapping("/{routeId}")
    public CustomResponse<ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO>> getReviews(
            @PathVariable Long routeId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size){
        return CustomResponse.onSuccess(reviewQueryService.getReviews(routeId, cursor, size));
    }

    @PostMapping("/{reviewId}")
    public CustomResponse<String> likeReview(@PathVariable Long reviewId){
        // TODO : 인가 완료되면 실제 user정보 넣기
        reviewCommandService.toggleLike(reviewId, 1L);
        return CustomResponse.onSuccess("리뷰 좋아요/취소가 완료됐습니다.");
    }

    @GetMapping("/me")
    public CustomResponse<ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.MyReviewResDTO>> getMyReviews(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size
    ){
        // TODO : 인가 완료되면 실제 user정보 넣기
        return CustomResponse.onSuccess(reviewQueryService.getMyReviews(1L, cursor, size));
    }

    @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponse<String> updateReview(
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long reviewId){
        reviewCommandService.updateReview(reviewId, 1L, content, images);
        return CustomResponse.onSuccess("리뷰 수정이 완료됐습니다.");
    }

    @DeleteMapping("/{reviewId}")
    public CustomResponse<String> deleteReview(@PathVariable Long reviewId){
        reviewCommandService.deleteReview(reviewId, 1L);
        return CustomResponse.onSuccess("리뷰 삭제가 완료됐습니다.");
    }
}
