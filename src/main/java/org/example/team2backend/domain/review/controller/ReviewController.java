package org.example.team2backend.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.service.command.ReviewCommandService;
import org.example.team2backend.domain.review.service.query.ReviewQueryService;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Tag(name = "리뷰 API", description = "리뷰 관련 API by 백종우")
public class ReviewController {
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @Operation(summary = "리뷰 작성", description = "특정 루트에 리뷰를 작성합니다.")
    @PostMapping(value = "/{routeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponse<ReviewResponseDTO.ReviewCreateResDTO> createReview(
            @Parameter(description = "리뷰 내용") @RequestPart("content") String content,
            @Parameter(description = "리뷰 이미지 (선택)") @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(description = "루트 ID") @PathVariable Long routeId,
            @AuthenticationPrincipal UserDetails userDetails){
        return CustomResponse.onSuccess(reviewCommandService.createReview(content, images, userDetails.getUsername(), routeId));
    }

    @Operation(summary = "리뷰 조회", description = "특정 루트의 리뷰 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/{routeId}")
    public CustomResponse<ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO>> getReviews(
            @Parameter(description = "루트 ID") @PathVariable Long routeId,
            @Parameter(description = "커서 (null 또는 0이면 첫 페이지)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return CustomResponse.onSuccess(reviewQueryService.getReviews(routeId, cursor, size, userDetails.getUsername()));
    }

    @Operation(summary = "리뷰 좋아요 토글", description = "리뷰에 좋아요/좋아요 취소를 합니다.")
    @PostMapping("/likes/{reviewId}")
    public CustomResponse<String> likeReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewCommandService.toggleLike(reviewId, userDetails.getUsername());
        return CustomResponse.onSuccess("리뷰 좋아요/취소가 완료됐습니다.");
    }

    @Operation(summary = "내가 작성한 리뷰 조회", description = "본인이 작성한 리뷰가 존재하는 장소를 커서 기반 페이지네이션 하여 조회합니다.")
    @GetMapping("/me")
     public CustomResponse<ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.MyReviewResDTO>> getMyReviews(
            @Parameter(description = "커서 (null 또는 0이면 첫 페이지)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
             @AuthenticationPrincipal UserDetails userDetails
     ){
          return CustomResponse.onSuccess(reviewQueryService.getMyReviews(userDetails.getUsername(), cursor, size));
     }

    @Operation(summary = "리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다.")
    @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomResponse<String> updateReview(
            @Parameter(description = "리뷰 내용") @RequestPart(value = "content") String content,
            @Parameter(description = "리뷰 이미지 (선택)") @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails){
        reviewCommandService.updateReview(reviewId, userDetails.getUsername(), content, images);
        return CustomResponse.onSuccess("리뷰 수정이 완료됐습니다.");
    }

    @Operation(summary = "리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public CustomResponse<String> deleteReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails){
        reviewCommandService.deleteReview(reviewId, userDetails.getUsername());
        return CustomResponse.onSuccess("리뷰 삭제가 완료됐습니다.");
    }
}
