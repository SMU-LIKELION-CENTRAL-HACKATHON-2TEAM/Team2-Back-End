package org.example.team2backend.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.review.converter.ReviewConverter;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.example.team2backend.domain.review.repository.ReviewImageRepository;
import org.example.team2backend.domain.review.repository.ReviewLikeRepository;
import org.example.team2backend.domain.review.repository.ReviewRepository;
import org.example.team2backend.domain.user.entity.User;
import org.example.team2backend.domain.user.repository.UserRepository;
import org.example.team2backend.global.apiPayload.code.ReviewErrorCode;
import org.example.team2backend.global.apiPayload.exception.ReviewException;
import org.example.team2backend.global.s3.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewResponseDTO.ReviewCreateResDTO createReview(String content, List<MultipartFile> images) {

        // 1. 리뷰 저장
        Review review = ReviewConverter.toReview(content);
        reviewRepository.save(review);

        // 2. 이미지 업로드 및 ReviewImage 저장
        if (images != null && !images.isEmpty()) {
            validateImageCount(images);
            images.forEach(image -> saveReviewImage(image, review));
        }

        return ReviewConverter.toReviewCreateResDTO(review);
    }

    public void toggleLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        User user = userRepository.findById(userId).get();
        // TODO : 유저 관련 구현 시 주석 풀기
//                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewAndUser(review, user);

        if (existingLike.isPresent()) {
            // 이미 좋아요 → 취소
            reviewLikeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            ReviewLike reviewLike = ReviewConverter.toReviewLike(review, user);
            reviewLikeRepository.save(reviewLike);
        }
    }

    public void updateReview(Long reviewId, Long userId, String content, List<MultipartFile> images) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        validateReviewUser(review, userId);

        // 내용 수정
        review.updateReview(content);

        // 기존 이미지 삭제 + S3에서 제거
        List<ReviewImage> existingImages = reviewImageRepository.findByReview(review);
        existingImages.stream().forEach(img -> {
            s3Service.deleteFile(img.getImageKey());
            reviewImageRepository.delete(img);
        });

        // 새 이미지 업로드
        if (images != null && !images.isEmpty()) {
            validateImageCount(images);
            images.forEach(image -> saveReviewImage(image, review));
        }
    }


    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 작성자 본인 확인
        validateReviewUser(review, userId);

        // 이미지 삭제
        List<ReviewImage> images = reviewImageRepository.findByReview(review);
        images.forEach(img -> s3Service.deleteFile(img.getImageKey()));
        reviewImageRepository.deleteAll(images);

        // 좋아요 삭제
        reviewLikeRepository.deleteByReview(review);

        // 리뷰 삭제
        reviewRepository.delete(review);
    }


    private void saveReviewImage(MultipartFile image, Review review) {
        String imageKey = s3Service.upload(image);
        String imageUrl = s3Service.getFileUrl(imageKey);

        ReviewImage reviewImage = ReviewConverter.toReviewImage(imageKey, imageUrl, review);
        reviewImageRepository.save(reviewImage);
    }

    private void validateImageCount(List<MultipartFile> images) {
        if (images.size() > 3) {
            throw new ReviewException(ReviewErrorCode.REVIEW_IMAGE_LIMIT);
        }
    }

    private void validateReviewUser(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)){
            throw new ReviewException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }
    }

}