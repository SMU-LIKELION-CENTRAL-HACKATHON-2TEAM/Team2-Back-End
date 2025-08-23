package org.example.team2backend.domain.member.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.converter.MemberRouteConverter;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.member.repository.MemberRouteRepository;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.exception.RouteErrorCode;
import org.example.team2backend.domain.route.exception.RouteException;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberRouteCommandService {

    private final MemberRepository memberRepository;
    private final RouteRepository routeRepository;
    private final MemberRouteRepository memberRouteRepository;

    //스크랩 추가 또는 삭제
    public void toggleScrap(String email, Long routeId) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        Optional<MemberRoute> ExistMr = memberRouteRepository.findMemberRouteByMemberAndRoute(member, route);

        //만약 테이블이 존재한다면
        if (ExistMr.isPresent()) {
            memberRouteRepository.delete(ExistMr.get());
            //루트 스크랩 수 1 감소
            routeRepository.decreaseBookmarked(routeId);
        } else {
            MemberRoute memberRoute = MemberRouteConverter.toMemberRoute(member, route);
            memberRouteRepository.save(memberRoute);
            //루트 스크랩 수 1 증가
            routeRepository.increaseBookmarked(routeId);
        }
    }
}
