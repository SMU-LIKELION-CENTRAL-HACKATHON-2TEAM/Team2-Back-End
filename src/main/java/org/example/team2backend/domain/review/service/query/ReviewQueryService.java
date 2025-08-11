package org.example.team2backend.domain.review.service.query;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.review.converter.ReviewConverter;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.repository.ReviewImageRepository;
import org.example.team2backend.domain.review.repository.ReviewRepository;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.example.team2backend.domain.user.entity.User;
import org.example.team2backend.domain.user.repository.UserRepository;
import org.example.team2backend.global.apiPayload.code.RouteErrorCode;
import org.example.team2backend.global.apiPayload.exception.RouteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public List<ReviewResponseDTO.ReviewResDTO> getReviews(Long routeId) {
        // 1. 루트 존재 확인
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        // 2. 해당 루트의 리뷰 조회
        List<Review> reviews = reviewRepository.findByRoute(route);

        // 3. 각 리뷰에 대한 이미지 조회 후 DTO 변환
        return reviews.stream()
                .map(review -> {
                    List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
                    return ReviewConverter.toReviewResDTO(review, reviewImages);
                })
                .toList();
    }

    public List<ReviewResponseDTO.MyReviewResDTO> getMyReviews(Long userId) {
        // 1. 유저 확인
        User user = userRepository.findById(userId).get();
                // TODO : User로직 완료되면 주석 풀기
//                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 2. 내가 쓴 리뷰 목록 조회
        List<Review> myReviews = reviewRepository.findByUser(user);

        // 3. DTO 변환
        return myReviews.stream()
                .map(review -> {
                    List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
                    return ReviewConverter.toMyReviewResDTO(review, reviewImages);
                })
                .toList();
    }
}
