package org.example.team2backend.domain.member.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.converter.MemberRouteConverter;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.member.exception.MemberErrorCode;
import org.example.team2backend.domain.member.exception.MemberException;
import org.example.team2backend.domain.member.exception.MemberRouteErrorCode;
import org.example.team2backend.domain.member.exception.MemberRouteException;
import org.example.team2backend.domain.member.repository.MemberRepository;
import org.example.team2backend.domain.member.repository.MemberRouteRepository;
import org.example.team2backend.domain.route.entity.Route;
import org.example.team2backend.domain.route.exception.RouteErrorCode;
import org.example.team2backend.domain.route.exception.RouteException;
import org.example.team2backend.domain.route.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberRouteCommandService {

    private final MemberRepository memberRepository;
    private final RouteRepository routeRepository;
    private final MemberRouteRepository memberRouteRepository;

    //스크랩 추가
    public void addScrap(String email, Long routeId) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        MemberRoute memberRoute = MemberRouteConverter.toMemberRoute(member, route);

        //이미 스크랩 했는 지 여부
        if (memberRouteRepository.findMemberRouteByMemberAndRoute(member, route).isEmpty()) {
            memberRouteRepository.save(memberRoute);
        } else {
            throw new IllegalArgumentException("이미 해당 회원이 동일한 루트로 등록했습니다.");
        }
    }

    //스크랩 삭제
    public void deleteScrap(String email, Long routeId) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException(RouteErrorCode.ROUTE_NOT_FOUND));

        MemberRoute memberRoute = MemberRouteConverter.toMemberRoute(member, route);

        if (memberRouteRepository.findMemberRouteByMemberAndRoute(member, route).isPresent()) {
            memberRouteRepository.save(memberRoute);
        } else {
            throw new MemberRouteException(MemberRouteErrorCode.MEMBER_ROUTE_NOT_FOUND);
        }



    }


}
