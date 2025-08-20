package org.example.team2backend.domain.member.converter;

import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.domain.member.entity.Member;
import org.example.team2backend.domain.member.entity.SocialType;
import org.example.team2backend.global.security.auth.Roles;

public class MemberConverter {

    public static Member toMember(MemberReqDTO.SignUpRequestDTO signUpRequestDTO) {
        return Member.builder()
                .email(signUpRequestDTO.email())
                .password(signUpRequestDTO.password())
                .nickname(signUpRequestDTO.nickname())
                .role(Roles.ROLE_USER)
                .socialType(SocialType.CUSTOM)
                .build();
    }


}
