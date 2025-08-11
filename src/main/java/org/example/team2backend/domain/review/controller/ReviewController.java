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
    public CustomResponse<List<ReviewResponseDTO.ReviewResDTO>> getReviews(@PathVariable Long routeId){
        return CustomResponse.onSuccess(reviewQueryService.getReviews(routeId));
    }
}
