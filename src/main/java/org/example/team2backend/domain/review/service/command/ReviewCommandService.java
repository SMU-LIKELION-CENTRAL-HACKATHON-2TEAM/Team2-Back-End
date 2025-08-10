package org.example.team2backend.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.review.converter.ReviewConverter;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.repository.ReviewImageRepository;
import org.example.team2backend.domain.review.repository.ReviewRepository;
import org.example.team2backend.global.s3.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;

    public ReviewResponseDTO.ReviewCreateResDTO createReview(String content, List<MultipartFile> images) {

        // 1. 리뷰 저장
        Review review = ReviewConverter.toReview(content);
        reviewRepository.save(review);

        // 2. 이미지 업로드 및 ReviewImage 저장
        if (images != null && !images.isEmpty()) {
            if (images.size() > 3) {
                throw new IllegalArgumentException("이미지는 최대 3개까지만 업로드할 수 있습니다.");
            }

            for (MultipartFile image : images) {
                String imageKey = s3Service.upload(image);
                String imageUrl = s3Service.getFileUrl(imageKey);

                ReviewImage reviewImage = ReviewConverter.toReviewImage(imageKey, imageUrl, review);
                reviewImageRepository.save(reviewImage);
            }
        }

        return ReviewConverter.toReviewCreateResDTO(review);
    }
}
