package org.example.team2backend.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.review.converter.ReviewConverter;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.entity.ReviewLike;
import org.example.team2backend.domain.review.repository.ReviewImageRepository;
import org.example.team2backend.domain.review.repository.ReviewLikeRepository;
import org.example.team2backend.domain.review.repository.ReviewRepository;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.example.team2backend.global.apiPayload.code.ReviewErrorCode;
import org.example.team2backend.global.apiPayload.code.RouteErrorCode;
import org.example.team2backend.global.apiPayload.exception.ReviewException;
import org.example.team2backend.global.apiPayload.exception.RouteException;
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
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final RouteRepository routeRepository;

    public ReviewResponseDTO.ReviewCreateResDTO createReview(String content, List<MultipartFile> images, String email, Long routeId) {

        // 1. 리뷰 저장
        Review review = ReviewConverter.toReview(content);
        Route route = routeRepository.findById(routeId).orElseThrow(()-> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));
        review.linkMember(getMember(email));
        review.linkRoute(route);
        reviewRepository.save(review);

        // 2. 이미지 업로드 및 ReviewImage 저장
        if (images != null && !images.isEmpty()) {
            validateImageCount(images);
            images.forEach(image -> saveReviewImage(image, review));
        }

        return ReviewConverter.toReviewCreateResDTO(review);
    }

    public void toggleLike(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        Member member = getMember(email);

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewAndMember(review, member);

        if (existingLike.isPresent()) {
            // 이미 좋아요 → 취소
            reviewLikeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            ReviewLike reviewLike = ReviewConverter.toReviewLike(review, member);
            reviewLikeRepository.save(reviewLike);
        }
    }

    public void updateReview(Long reviewId, String email, String content, List<MultipartFile> images) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        validateReviewMember(review, getMember(email).getId());

        // 내용 수정
        review.updateReview(content);

        // 기존 이미지 삭제 + S3에서 제거
        List<ReviewImage> existingImages = reviewImageRepository.findByReview(review);
        existingImages.forEach(img -> {
            s3Service.deleteFile(img.getImageKey());
            reviewImageRepository.delete(img);
        });

        // 새 이미지 업로드
        if (images != null && !images.isEmpty()) {
            validateImageCount(images);
            images.forEach(image -> saveReviewImage(image, review));
        }
    }


    public void deleteReview(Long reviewId, String email) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 작성자 본인 확인
        validateReviewMember(review, getMember(email).getId());

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
        String imageKey = s3Service.upload(image, "review");
        String imageUrl = s3Service.getFileUrl(imageKey);

        ReviewImage reviewImage = ReviewConverter.toReviewImage(imageKey, imageUrl, review);
        reviewImageRepository.save(reviewImage);
    }

    private void validateImageCount(List<MultipartFile> images) {
        if (images.size() > 3) {
            throw new ReviewException(ReviewErrorCode.REVIEW_IMAGE_LIMIT);
        }
    }

    private void validateReviewMember(Review review, Long memberId) {
        if (!review.getMember().getId().equals(memberId)){
            throw new ReviewException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }
    }

    private Member getMember(String email){
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }


}