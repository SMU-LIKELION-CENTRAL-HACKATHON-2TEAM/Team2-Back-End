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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO> getReviews(Long routeId, Long cursor, int size) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size);
        Long effectiveCursorId = getEffectiveCursorId(cursor);

        Slice<Review> slice = reviewRepository
                .findByRouteAndIdLessThanOrderByIdDesc(route, effectiveCursorId, pageable);

        // 리뷰별 이미지 조회
        Map<Long, List<ReviewImage>> imageMap = slice.getContent().stream()
                .collect(Collectors.toMap(
                        Review::getId,
                        reviewImageRepository::findByReview
                ));

        return ReviewConverter.toReviewSliceResponse(slice, imageMap);
    }

    public ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.MyReviewResDTO> getMyReviews(Long userId, Long cursor, int size) {
        // TODO : User 부분 완성 시 구현
        User user = userRepository.findById(userId).get();
//                .orElseThrow(() -> new UserE(UserErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size);
        Long effectiveCursorId = getEffectiveCursorId(cursor);

        Slice<Review> slice = reviewRepository
                .findByUserAndIdLessThanOrderByIdDesc(user, effectiveCursorId, pageable);

        Map<Long, List<ReviewImage>> imageMap = slice.getContent().stream()
                .collect(Collectors.toMap(
                        Review::getId,
                        reviewImageRepository::findByReview
                ));

        return ReviewConverter.toMyReviewSliceResponse(slice, imageMap);
    }

    private Long getEffectiveCursorId(Long cursorId) {
        return (cursorId == null || cursorId == 0) ? Long.MAX_VALUE : cursorId;
    }
}
