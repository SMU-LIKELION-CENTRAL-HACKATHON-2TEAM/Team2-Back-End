package org.example.team2backend.domain.review.service.query;

import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.review.converter.ReviewConverter;
import org.example.team2backend.domain.review.dto.response.ReviewResponseDTO;
import org.example.team2backend.domain.review.entity.Review;
import org.example.team2backend.domain.review.entity.ReviewImage;
import org.example.team2backend.domain.review.repository.ReviewImageRepository;
import org.example.team2backend.domain.review.repository.ReviewLikeRepository;
import org.example.team2backend.domain.review.repository.ReviewRepository;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.example.team2backend.global.apiPayload.code.RouteErrorCode;
import org.example.team2backend.global.apiPayload.exception.RouteException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.ReviewResDTO> getReviews(Long routeId, Long cursor, int size, String email) {
        Member member = getMember(email);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size);
        Long effectiveCursorId = getEffectiveCursorId(cursor);

        // 첫 페이지 판단 로직 수정
        boolean isFirstPage = (cursor == null || cursor <= 0);

        // 1. 현재 사용자의 리뷰 조회 (첫 페이지일 때만)
        List<Review> myReviews = new ArrayList<>();
        if (isFirstPage && member.getId() != null) {
            myReviews = reviewRepository.findByRouteAndMemberIdOrderByIdDesc(route, member.getId());
        }

        // 2. 다른 사용자들의 리뷰 조회 (내 리뷰 제외)
        Slice<Review> otherReviews;
        if (member.getId() != null) {
            otherReviews = reviewRepository
                    .findByRouteAndMemberIdNotAndIdLessThanOrderByIdDesc(route, member.getId(), effectiveCursorId, pageable);
        } else {
            otherReviews = reviewRepository
                    .findByRouteAndIdLessThanOrderByIdDesc(route, effectiveCursorId, pageable);
        }

        // 3. 리뷰 리스트 합치기
        List<Review> combinedReviews = new ArrayList<>();
        if (isFirstPage) {
            combinedReviews.addAll(myReviews);
        }
        combinedReviews.addAll(otherReviews.getContent());

        // 4. 이미지 조회
        Map<Long, List<ReviewImage>> imageMap = combinedReviews.stream()
                .collect(Collectors.toMap(
                        Review::getId,
                        reviewImageRepository::findByReview
                ));

        Map<Long, Long> likeCountMap = reviewLikeRepository.countMapByReviewIn(combinedReviews);

        // 5. Slice 객체 재구성
        boolean hasNext = otherReviews.hasNext();
        Slice<Review> finalSlice = new SliceImpl<>(combinedReviews, pageable, hasNext);

        return ReviewConverter.toReviewSliceResponse(finalSlice, imageMap, likeCountMap, member.getId());
    }

    public ReviewResponseDTO.CursorResDTO<ReviewResponseDTO.MyReviewResDTO> getMyReviews(String email, Long cursor, int size) {

        Member member = getMember(email);

        Pageable pageable = PageRequest.of(0, size);
        Long effectiveCursorId = getEffectiveCursorId(cursor);

        Slice<Review> slice = reviewRepository
                .findByMemberAndIdLessThanOrderByIdDesc(member, effectiveCursorId, pageable);

        Map<Long, List<ReviewImage>> imageMap = slice.getContent().stream()
                .collect(Collectors.toMap(
                        Review::getId,
                        reviewImageRepository::findByReview
                ));

        return ReviewConverter.toMyReviewSliceResponse(slice, imageMap);
    }

    private Long getEffectiveCursorId(Long cursor) {
        if (cursor == null || cursor <= 0) {
            return Long.MAX_VALUE;
        }
        return cursor;
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
