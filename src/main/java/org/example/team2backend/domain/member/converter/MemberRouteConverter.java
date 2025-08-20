package org.example.team2backend.domain.member.converter;

import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.MemberRoute;
import org.example.team2backend.domain.route.entity.Route;

public class MemberRouteConverter {

    public static MemberRoute toMemberRoute(Member member, Route route) {
        return MemberRoute.builder()
                .member(member)
                .route(route)
                .build();
    }

}
